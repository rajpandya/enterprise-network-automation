import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Device } from '../models/device';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class DeviceService {

  private readonly apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getDevices(): Observable<Device[]> {
    return this.http.get<Device[]>(this.apiUrl);
  }

  runHealthCheck(): Observable<Device[]> {
    console.log('DeviceService.runHealthCheck() called');

    return this.http.get<Device[]>(
      `${this.apiUrl}/health-check`
    );
  }

}