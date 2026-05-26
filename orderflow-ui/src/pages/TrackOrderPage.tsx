import { FormEvent, useState } from 'react';
import { useNavigate } from 'react-router-dom';

export default function TrackOrderPage() {
  const navigate = useNavigate();
  const [orderId, setOrderId] = useState('');

  const handleSubmit = (event: FormEvent) => {
    event.preventDefault();
    const trimmed = orderId.trim();
    if (!trimmed) {
      return;
    }
    navigate(`/orders/${encodeURIComponent(trimmed)}`);
  };

  return (
    <>
      <h2 className="page-title">Track your order</h2>
      <p className="page-subtitle">Enter the order ID from your confirmation screen.</p>

      <section className="card track-form-card">
        <form onSubmit={handleSubmit}>
          <div className="field">
            <label htmlFor="orderId">Order ID</label>
            <input
              id="orderId"
              value={orderId}
              onChange={(e) => setOrderId(e.target.value)}
              placeholder="e.g. 8f3c2a1b-..."
              autoComplete="off"
            />
          </div>
          <button type="submit" className="btn btn-primary" disabled={!orderId.trim()}>
            View order &amp; tracking
          </button>
        </form>
      </section>
    </>
  );
}
