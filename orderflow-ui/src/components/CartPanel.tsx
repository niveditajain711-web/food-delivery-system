import type { MenuItem, OrderLine } from '../types';

interface CartPanelProps {
  menu: MenuItem[];
  lines: OrderLine[];
  customerId: string;
  onCustomerIdChange: (value: string) => void;
  onPlaceOrder: () => void;
  placing: boolean;
}

export default function CartPanel({
  menu,
  lines,
  customerId,
  onCustomerIdChange,
  onPlaceOrder,
  placing,
}: CartPanelProps) {
  const nameById = Object.fromEntries(menu.map((m) => [m.productId, m.name]));
  const totalItems = lines.reduce((sum, l) => sum + l.quantity, 0);

  return (
    <aside className="card">
      <h2>Your order</h2>
      <div className="field">
        <label htmlFor="customerId">Customer ID</label>
        <input
          id="customerId"
          value={customerId}
          onChange={(e) => onCustomerIdChange(e.target.value)}
          placeholder="e.g. cust-1"
        />
      </div>
      {lines.length === 0 ? (
        <p className="cart-empty">Add items from the menu</p>
      ) : (
        <ul className="cart-lines">
          {lines.map((line) => (
            <li key={line.productId}>
              <span>
                {nameById[line.productId] ?? line.productId} × {line.quantity}
              </span>
            </li>
          ))}
        </ul>
      )}
      <button
        type="button"
        className="btn btn-primary"
        onClick={onPlaceOrder}
        disabled={placing || lines.length === 0 || !customerId.trim()}
      >
        {placing ? 'Placing…' : `Place order (${totalItems} items)`}
      </button>
    </aside>
  );
}
