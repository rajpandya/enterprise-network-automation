"use client";

import { useEffect, useState } from "react";

const API_URL = process.env.NEXT_PUBLIC_API_URL;

type Device = {
  hostname: string;
  ip: string;
  status: string;
  latency: number;
  deviceType: string;
};

export default function Home() {
  const [devices, setDevices] = useState<Device[]>([]);
  const [error, setError] = useState("");

  const [hostname, setHostname] = useState("");
  const [ip, setIp] = useState("");
  const [deviceType, setDeviceType] = useState("");

  const [editingIp, setEditingIp] = useState<string | null>(null);
  const [editHostname, setEditHostname] = useState("");
  const [editStatus, setEditStatus] = useState("");
  const [editLatency, setEditLatency] = useState(0);
  const [editDeviceType, setEditDeviceType] = useState("");

  const [loading, setLoading] = useState(false);

  async function getErrorMessage(response: Response) {
    try {
      const data = await response.json();

      return (
        data.message ||
        data.error ||
        `Request failed with status ${response.status}`
      );
    } catch {
      return `Request failed with status ${response.status}`;
    }
  }

  useEffect(() => {
    async function loadDevices() {
      try {
        setError("");

        const response = await fetch(`${API_URL}/api/devices`);

        if (!response.ok) {
          const message = await getErrorMessage(response);
          throw new Error(message);
        }

        const data: Device[] = await response.json();
        setDevices(data);
      } catch (error) {
        if (error instanceof Error) {
          setError(error.message);
        }
      }
    }

    loadDevices();
  }, []);

  async function runHealthCheck() {
    try {
      setLoading(true);
      setError("");

      const response = await fetch(`${API_URL}/api/devices/health-check`);

      if (!response.ok) {
        const message = await getErrorMessage(response);
        throw new Error(message);
      }

      const data: Device[] = await response.json();
      setDevices(data);
    } catch (error) {
      if (error instanceof Error) {
        setError(error.message);
      }
    } finally {
      setLoading(false);
    }
  }

  async function addDevice() {
    try {
      setError("");

      const response = await fetch(`${API_URL}/api/devices`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          hostname,
          ip,
          status: "UNKNOWN",
          latency: 0,
          deviceType,
        }),
      });

      if (!response.ok) {
        const message = await getErrorMessage(response);
        throw new Error(message);
      }

      const newDevice: Device = await response.json();

      setDevices((currentDevices) => [...currentDevices, newDevice]);

      setHostname("");
      setIp("");
      setDeviceType("");
    } catch (error) {
      if (error instanceof Error) {
        setError(error.message);
      }
    }
  }

  async function deleteDevice(ip: string) {
    try {
      setError("");

      const response = await fetch(`${API_URL}/api/devices/${ip}`, {
        method: "DELETE",
      });

      if (!response.ok) {
        const message = await getErrorMessage(response);
        throw new Error(message);
      }

      setDevices((currentDevices) =>
        currentDevices.filter((device) => device.ip !== ip),
      );
    } catch (error) {
      if (error instanceof Error) {
        setError(error.message);
      }
    }
  }

  function startEdit(device: Device) {
    setEditingIp(device.ip);
    setEditHostname(device.hostname);
    setEditStatus(device.status);
    setEditLatency(device.latency);
    setEditDeviceType(device.deviceType);
  }

  async function updateDevice(ip: string) {
    try {
      setError("");

      const response = await fetch(`${API_URL}/api/devices/${ip}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          hostname: editHostname,
          ip,
          status: editStatus,
          latency: editLatency,
          deviceType: editDeviceType,
        }),
      });

      if (!response.ok) {
        const message = await getErrorMessage(response);
        throw new Error(message);
      }

      const updatedDevice: Device = await response.json();

      setDevices((currentDevices) =>
        currentDevices.map((device) =>
          device.ip === ip ? updatedDevice : device,
        ),
      );

      setEditingIp(null);
    } catch (error) {
      if (error instanceof Error) {
        setError(error.message);
      }
    }
  }

  return (
    <main
      style={{
        padding: "40px",
        fontFamily: "Arial",
        maxWidth: "1200px",
        margin: "0 auto",
      }}
    >
      <h1>Network Device Inventory</h1>

      <button
        onClick={runHealthCheck}
        disabled={loading}
        style={{
          marginTop: "20px",
          padding: "11px 18px",
          backgroundColor: loading ? "#9ca3af" : "#16a34a",
          color: "white",
          border: "none",
          borderRadius: "6px",
          fontSize: "15px",
          fontWeight: "600",
          cursor: loading ? "not-allowed" : "pointer",
        }}
      >
        {loading ? "Checking Devices..." : "Run Health Check"}
      </button>

      <h2
        style={{
          marginTop: "30px",
          marginBottom: "15px",
        }}
      >
        Add Device
      </h2>

      <div
        style={{
          display: "flex",
          gap: "12px",
          flexWrap: "wrap",
          alignItems: "center",
          padding: "20px",
          border: "1px solid #d0d7de",
          borderRadius: "8px",
          backgroundColor: "#f8f9fa",
          marginBottom: "25px",
        }}
      >
        <input
          type="text"
          placeholder="Hostname"
          value={hostname}
          onChange={(e) => setHostname(e.target.value)}
          style={{
            padding: "10px 12px",
            border: "1px solid #b8c0c8",
            borderRadius: "6px",
            fontSize: "15px",
            minWidth: "180px",
          }}
        />

        <input
          type="text"
          placeholder="IP Address"
          value={ip}
          onChange={(e) => setIp(e.target.value)}
          style={{
            padding: "10px 12px",
            border: "1px solid #b8c0c8",
            borderRadius: "6px",
            fontSize: "15px",
            minWidth: "180px",
          }}
        />

        <input
          type="text"
          placeholder="Device Type"
          value={deviceType}
          onChange={(e) => setDeviceType(e.target.value)}
          style={{
            padding: "10px 12px",
            border: "1px solid #b8c0c8",
            borderRadius: "6px",
            fontSize: "15px",
            minWidth: "180px",
          }}
        />

        <button
          onClick={addDevice}
          style={{
            padding: "11px 18px",
            backgroundColor: "#2563eb",
            color: "white",
            border: "none",
            borderRadius: "6px",
            fontSize: "15px",
            fontWeight: "600",
            cursor: "pointer",
          }}
        >
          Add Device
        </button>
      </div>

      {error && (
        <div
          style={{
            marginTop: "20px",
            marginBottom: "20px",
            padding: "12px",
            backgroundColor: "#fee2e2",
            color: "#991b1b",
            borderRadius: "6px",
            border: "1px solid #fecaca",
          }}
        >
          {error}
        </div>
      )}

      <table
        border={1}
        cellPadding={10}
        style={{
          borderCollapse: "collapse",
          marginTop: "20px",
          width: "100%",
        }}
      >
        <thead>
          <tr>
            <th>Hostname</th>
            <th>IP Address</th>
            <th>Type</th>
            <th>Status</th>
            <th>Latency</th>
            <th>Actions</th>
          </tr>
        </thead>

        <tbody>
          {devices.map((device) => (
            <tr key={device.ip}>
              {editingIp === device.ip ? (
                <>
                  <td>
                    <input
                      value={editHostname}
                      onChange={(e) => setEditHostname(e.target.value)}
                    />
                  </td>

                  <td>{device.ip}</td>

                  <td>
                    <input
                      value={editDeviceType}
                      onChange={(e) => setEditDeviceType(e.target.value)}
                    />
                  </td>

                  <td>
                    <input
                      value={editStatus}
                      onChange={(e) => setEditStatus(e.target.value)}
                    />
                  </td>

                  <td>
                    <input
                      type="number"
                      value={editLatency}
                      onChange={(e) => setEditLatency(Number(e.target.value))}
                    />
                  </td>

                  <td>
                    <button
                      onClick={() => updateDevice(device.ip)}
                      style={{
                        padding: "7px 12px",
                        backgroundColor: "#16a34a",
                        color: "white",
                        border: "none",
                        borderRadius: "5px",
                        fontWeight: "600",
                        cursor: "pointer",
                        marginRight: "8px",
                      }}
                    >
                      Save
                    </button>

                    <button
                      onClick={() => setEditingIp(null)}
                      style={{
                        padding: "7px 12px",
                        backgroundColor: "#6b7280",
                        color: "white",
                        border: "none",
                        borderRadius: "5px",
                        fontWeight: "600",
                        cursor: "pointer",
                      }}
                    >
                      Cancel
                    </button>
                  </td>
                </>
              ) : (
                <>
                  <td>{device.hostname}</td>
                  <td>{device.ip}</td>
                  <td>{device.deviceType}</td>

                  <td>
                    <span
                      style={{
                        display: "inline-block",
                        padding: "4px 10px",
                        borderRadius: "12px",
                        fontWeight: "600",
                        fontSize: "13px",
                        backgroundColor:
                          device.status === "ONLINE"
                            ? "#dcfce7"
                            : device.status === "OFFLINE"
                              ? "#fee2e2"
                              : "#f3f4f6",
                        color:
                          device.status === "ONLINE"
                            ? "#166534"
                            : device.status === "OFFLINE"
                              ? "#991b1b"
                              : "#374151",
                      }}
                    >
                      {device.status}
                    </span>
                  </td>

                  <td>{device.latency} ms</td>

                  <td>
                    <button
                      onClick={() => startEdit(device)}
                      style={{
                        padding: "7px 12px",
                        backgroundColor: "#2563eb",
                        color: "white",
                        border: "none",
                        borderRadius: "5px",
                        fontWeight: "600",
                        cursor: "pointer",
                        marginRight: "8px",
                      }}
                    >
                      Edit
                    </button>

                    <button
                      onClick={() => {
                        const confirmed = window.confirm(
                          `Are you sure you want to delete ${device.hostname}?`,
                        );

                        if (confirmed) {
                          deleteDevice(device.ip);
                        }
                      }}
                      style={{
                        padding: "7px 12px",
                        backgroundColor: "#dc2626",
                        color: "white",
                        border: "none",
                        borderRadius: "5px",
                        fontWeight: "600",
                        cursor: "pointer",
                      }}
                    >
                      Delete
                    </button>
                  </td>
                </>
              )}
            </tr>
          ))}
        </tbody>
      </table>
    </main>
  );
}
