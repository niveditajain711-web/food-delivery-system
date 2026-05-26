import { useEffect, useState } from 'react';
import { Link, useLocation, useParams } from 'react-router-dom';
import { fetchMenu, fetchOrder } from '../api';
import TrackingPanel from '../components/TrackingPanel';
import { formatStatus, productName, restaurantName } from '../utils/labels';
import type { MenuItem, Order } from '../types';

function statusClass(status: string): string {
  if (status === 'DELIVERED') return 'status-badge status-delivered';
  if (status === 'OUT_FOR_DELIVERY') return 'status-badge status-out-for-delivery';
  return 'status-badge status-confirmed';
}

export default function OrderDetailPage() {
  const { orderId } = useParams<{ orderId: string }>();
  const location = useLocation();
  const justPlaced = Boolean((location.state as { justPlaced?: boolean } | null)?.justPlaced);

  const [order, setOrder] = useState<Order | null>(null);
  const [menu, setMenu] = useState<MenuItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!orderId) return;
    setLoading(true);
    setError(null);
    fetchOrder(orderId)
      .then((o) => {
        setOrder(o);
        return fetchMenu(o.restaurantId);
      })
      .then(setMenu)
      .catch((e: Error) => setError(e.message))
      .finally(() => setLoading(false));
  }, [orderId]);

  if (!orderId) {
    return <div className="alert alert-error">Missing order ID</div>;
  }

  if (loading) {
    return <p className="loading">Loading order…</p>;
  }

  if (error || !order) {
    return (
      <>
        <div className="alert alert-error">{error ?? 'Order not found'}</div>
        <Link to="/track" className="btn btn-secondary">
          Try another order ID
        </Link>
      </>
    );
  }

  return (
    <>
      {justPlaced && (
        <div className="alert alert-success banner-success">
          Order placed successfully. Live tracking starts below.
        </div>
      )}

      <h2 className="page-title">Order details</h2>
      <p className="page-subtitle">
        <Link to="/">← Order more food</Link>
        {' · '}
        <Link to="/track">Track another order</Link>
      </p>

      <div className="layout-two-col">
        <section className="card">
          <h2>Summary</h2>
          <dl className="order-meta">
            <dt>Order ID</dt>
            <dd className="mono">{order.orderId}</dd>
            <dt>Status</dt>
            <dd>
              <span className={statusClass(order.status)}>{formatStatus(order.status)}</span>
            </dd>
            <dt>Customer</dt>
            <dd>{order.customerId}</dd>
            <dt>Restaurant</dt>
            <dd>{restaurantName(order.restaurantId)}</dd>
          </dl>
          <h2 className="section-heading">Items</h2>
          <ul className="cart-lines">
            {order.items.map((line) => (
              <li key={line.productId}>
                <span>
                  {productName(line.productId, menu)} × {line.quantity}
                </span>
              </li>
            ))}
          </ul>
        </section>

        <TrackingPanel orderId={orderId} autoStart />
      </div>
    </>
  );
}
