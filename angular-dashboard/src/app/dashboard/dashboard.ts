import { Component, OnInit, inject } from '@angular/core';
import { Device } from '../models/device';
import { DeviceService } from '../services/device';

@Component({
  imports: [],
  selector: 'app-dashboard',
  styleUrl: './dashboard.css',
  templateUrl: './dashboard.html',
})
export class Dashboard implements OnInit {

  isLoading = false;
  errorMessage = '';

  private readonly deviceService = inject(DeviceService);

  devices: Device[] = [];

  totalDevices = 0;
  onlineDevices = 0;
  offlineDevices = 0;
  averageLatency = 0;

  ngOnInit(): void {
    this.loadDevices();
  }

  loadDevices(): void {
  this.isLoading = true;
  this.errorMessage = '';

  this.deviceService.getDevices().subscribe({
    next: (devices) => {
      this.devices = devices;

      this.totalDevices = devices.length;
      this.onlineDevices = devices.filter(
        device => device.status === 'ONLINE'
      ).length;

      this.offlineDevices = devices.filter(
        device => device.status === 'OFFLINE'
      ).length;

      if (devices.length > 0) {
        const totalLatency = devices.reduce(
          (sum, device) => sum + device.latency,
          0
        );

        this.averageLatency = Math.round(
          totalLatency / devices.length
        );
      }

      this.isLoading = false;
    },

    error: (error) => {
      console.error('Error loading devices:', error);

      this.errorMessage = 'Unable to load devices.';
      this.isLoading = false;
    }
  });
}

runHealthCheck(): void {
  console.log('Run Health Check clicked');

  this.deviceService.runHealthCheck().subscribe({
    next: (devices) => {
      console.log('Health check response:', devices);

      this.devices = devices;

      this.totalDevices = devices.length;
      this.onlineDevices = devices.filter(
        device => device.status === 'ONLINE'
      ).length;

      this.offlineDevices = devices.filter(
        device => device.status === 'OFFLINE'
      ).length;

      if (devices.length > 0) {
        const totalLatency = devices.reduce(
          (sum, device) => sum + device.latency,
          0
        );

        this.averageLatency = Math.round(
          totalLatency / devices.length
        );
      }

      console.log('Health check completed');
    },

    error: (error) => {
      console.error('Health check failed:', error);
    },

    complete: () => {
      console.log('Health check observable completed');
    }
  });
}

}