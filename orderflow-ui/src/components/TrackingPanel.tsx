import { useEffect, useState } from 'react';
import { subscribeToTracking } from '../api';
import type { LocationUpdate } from '../types';

interface TrackingPanelProps {
  orderId: string;
  /** Start SSE stream when the panel mounts (order confirmation page). */
  autoStart?: boolean;
}

const TOTAL_STEPS = 5;

function toMapPosition(lat: number, lng: number): { left: string; top: string } {
  const minLat = 12.9352;
  const maxLat = 12.9716;
  const minLng = 77.5946;
  const maxLng = 77.6245;
  const left = ((lng - minLng) / (maxLng - minLng)) * 80 + 10;
  const top = (1 - (lat - minLat) / (maxLat - minLat)) * 70 + 15;
  return { left: `${left}%`, top: `${top}%` };
}

export default function TrackingPanel({ orderId, autoStart = false }: TrackingPanelProps) {
  const [updates, setUpdates] = useState<LocationUpdate[]>([]);
  const [active, setActive] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [done, setDone] = useState(false);
  const [session, setSession] = useState(0);

  const latest = updates[updates.length - 1];
  const progress = latest ? Math.min(100, (latest.sequence / TOTAL_STEPS) * 100) : 0;
  const dotPos = latest ? toMapPosition(latest.latitude, latest.longitude) : { left: '15%', top: '75%' };

  const restart = () => {
    setUpdates([]);
    setError(null);
    setDone(false);
    setSession((s) => s + 1);
    setActive(true);
  };

  useEffect(() => {
    if (autoStart) {
      setActive(true);
    }
  }, [autoStart, orderId]);

  useEffect(() => {
    if (!active) {
      return;
    }
    const unsubscribe = subscribeToTracking(
      orderId,
      (update) => setUpdates((prev) => [...prev, update]),
      () => {
        setActive(false);
        setDone(true);
      },
      (message) => {
        setError(message);
        setActive(false);
      }
    );
    return unsubscribe;
  }, [orderId, active, session]);

  return (
    <section className="card">
      <h2>Live delivery</h2>
      {error && <div className="alert alert-error">{error}</div>}
      {done && <div className="alert alert-success">Delivered — enjoy your meal!</div>}

      <div className="progress-bar" role="progressbar" aria-valuenow={progress} aria-valuemin={0} aria-valuemax={100}>
        <div className="progress-fill" style={{ width: `${progress}%` }} />
      </div>
      <p className="progress-label">
        {active ? 'Driver on the way…' : done ? 'Delivery complete' : 'Waiting to start'}
      </p>

      <div className="tracking-map" aria-label="Delivery map">
        <div className="tracking-route" />
        <span className="map-label map-label-start">Restaurant</span>
        <span className="map-label map-label-end">You</span>
        <div className="tracking-dot" style={{ left: dotPos.left, top: dotPos.top }} title="Driver" />
      </div>

      <ul className="tracking-log">
        {updates.length === 0 && !active && <li>Tracking updates will appear here.</li>}
        {updates.map((u) => (
          <li key={u.sequence}>
            <strong>Stop {u.sequence}</strong> — {u.latitude.toFixed(4)}, {u.longitude.toFixed(4)}
            {u.delivered ? ' · Arrived' : ''}
          </li>
        ))}
      </ul>

      {!autoStart && (
        <button type="button" className="btn btn-secondary" onClick={restart} disabled={active} style={{ marginTop: '0.5rem' }}>
          {active ? 'Tracking…' : done ? 'Track again' : 'Start tracking'}
        </button>
      )}
      {autoStart && done && (
        <button type="button" className="btn btn-secondary" onClick={restart} style={{ marginTop: '0.5rem' }}>
          Watch delivery again
        </button>
      )}
    </section>
  );
}
