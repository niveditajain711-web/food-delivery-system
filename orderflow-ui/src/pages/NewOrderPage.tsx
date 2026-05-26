import { useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { fetchMenu, placeOrder } from '../api';
import CartPanel from '../components/CartPanel';
import MenuGrid from '../components/MenuGrid';
import { RESTAURANTS, type MenuItem, type OrderLine } from '../types';

export default function NewOrderPage() {
  const navigate = useNavigate();
  const [restaurantId, setRestaurantId] = useState<string>(RESTAURANTS[0].id);
  const [menu, setMenu] = useState<MenuItem[]>([]);
  const [quantities, setQuantities] = useState<Record<string, number>>({});
  const [customerId, setCustomerId] = useState('cust-1');
  const [loading, setLoading] = useState(true);
  const [placing, setPlacing] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setLoading(true);
    setError(null);
    fetchMenu(restaurantId)
      .then(setMenu)
      .catch((e: Error) => setError(e.message))
      .finally(() => setLoading(false));
  }, [restaurantId]);

  const onRestaurantChange = (id: string) => {
    setRestaurantId(id);
    setQuantities({});
  };

  const lines: OrderLine[] = useMemo(
    () =>
      Object.entries(quantities)
        .filter(([, qty]) => qty > 0)
        .map(([productId, quantity]) => ({ productId, quantity })),
    [quantities]
  );

  const addItem = useCallback((productId: string) => {
    setQuantities((q) => ({ ...q, [productId]: (q[productId] ?? 0) + 1 }));
  }, []);

  const removeItem = useCallback((productId: string) => {
    setQuantities((q) => {
      const next = { ...q };
      const current = next[productId] ?? 0;
      if (current <= 1) {
        delete next[productId];
      } else {
        next[productId] = current - 1;
      }
      return next;
    });
  }, []);

  const handlePlaceOrder = async () => {
    setPlacing(true);
    setError(null);
    try {
      const response = await placeOrder({ customerId, restaurantId, items: lines });
      navigate(`/orders/${response.orderId}`, { state: { justPlaced: true } });
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to place order');
    } finally {
      setPlacing(false);
    }
  };

  const activeRestaurant = RESTAURANTS.find((r) => r.id === restaurantId);

  return (
    <>
      <h2 className="page-title">Order food</h2>
      <p className="page-subtitle">
        Choose a restaurant, add items to your cart, and place your order.
      </p>

      {error && <div className="alert alert-error">{error}</div>}

      <div className="restaurant-tabs" role="tablist" aria-label="Restaurants">
        {RESTAURANTS.map((r) => (
          <button
            key={r.id}
            type="button"
            role="tab"
            aria-selected={restaurantId === r.id}
            className={`tab ${restaurantId === r.id ? 'active' : ''}`}
            onClick={() => onRestaurantChange(r.id)}
          >
            {r.name}
          </button>
        ))}
      </div>

      <div className="layout-two-col">
        <section className="card">
          <h2>{activeRestaurant?.name ?? 'Menu'}</h2>
          {loading ? (
            <p className="loading">Loading menu…</p>
          ) : (
            <MenuGrid items={menu} quantities={quantities} onAdd={addItem} onRemove={removeItem} />
          )}
        </section>

        <CartPanel
          menu={menu}
          lines={lines}
          customerId={customerId}
          onCustomerIdChange={setCustomerId}
          onPlaceOrder={handlePlaceOrder}
          placing={placing}
        />
      </div>
    </>
  );
}
