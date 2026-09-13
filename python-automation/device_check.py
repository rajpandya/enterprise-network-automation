import platform
import subprocess
import requests

API_URL = "http://localhost:8080/api/devices"

def get_devices():
    response = requests.get(API_URL, timeout=5)
    response.raise_for_status()
    return response.json()

def ping_device(ip):
    count_flag = "-n" if platform.system().lower() == "windows" else "-c"

    try:
        result = subprocess.run(
            ["ping", count_flag, "1", ip],
            capture_output=True,
            text=True,
            timeout=5
        )

        if result.returncode != 0:
            return False, 0

        output = result.stdout

        # macOS/Linux output contains something like: time=12.345 ms
        if "time=" in output:
            time_part = output.split("time=")[1].split(" ")[0]
            latency = round(float(time_part))
        else:
            latency = 0

        return True, latency

    except subprocess.TimeoutExpired:
        return False, 0

def update_device_status(ip, status, latency):
    url = f"{API_URL}/{ip}"

    payload = {
        "status": status,
        "latency": latency
    }

    response = requests.patch(
        url,
        json=payload,
        timeout=5
    )

    response.raise_for_status()
    return response.json()

def main():
    devices = get_devices()

    print(f"Checking {len(devices)} devices...\n")

    for device in devices:
        ip = device["ip"]
        hostname = device["hostname"]

        reachable, latency = ping_device(ip)

        status = "ONLINE" if reachable else "OFFLINE"

        updated_device = update_device_status(
            ip,
            status,
            latency
        )

        print(
            f"{hostname} | "
            f"{ip} | "
            f"{updated_device['status']} | "
            f"{updated_device['latency']} ms"
        )

if __name__ == "__main__":
    main()