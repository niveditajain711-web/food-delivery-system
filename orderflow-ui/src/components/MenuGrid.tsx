import { productIcon } from '../utils/labels';
import type { MenuItem } from '../types';

interface CartQuantities {
  [productId: string]: number;
}

interface MenuGridProps {
  items: MenuItem[];
  quantities: CartQuantities;
  onAdd: (productId: string) => void;
  onRemove: (productId: string) => void;
}

export default function MenuGrid({ items, quantities, onAdd, onRemove }: MenuGridProps) {
  if (items.length === 0) {
    return <p className="cart-empty">No items for this restaurant.</p>;
  }

  return (
    <div className="menu-grid">
      {items.map((item) => {
        const qty = quantities[item.productId] ?? 0;
        return (
          <article key={item.productId} className="menu-item">
            <div className="menu-item-icon" aria-hidden>
              {productIcon(item.productId)}
            </div>
            <h3>{item.name}</h3>
            <div className="menu-item-actions">
              <div className="qty-control">
                <button
                  type="button"
                  onClick={() => onRemove(item.productId)}
                  disabled={qty === 0}
                  aria-label={`Remove one ${item.name}`}
                >
                  −
                </button>
                <span aria-live="polite">{qty}</span>
                <button
                  type="button"
                  onClick={() => onAdd(item.productId)}
                  aria-label={`Add one ${item.name}`}
                >
                  +
                </button>
              </div>
            </div>
          </article>
        );
      })}
    </div>
  );
}
