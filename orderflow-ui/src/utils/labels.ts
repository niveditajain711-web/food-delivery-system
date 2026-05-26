import { RESTAURANTS, type MenuItem } from '../types';

const PRODUCT_NAMES: Record<string, string> = {
  'burger-1': 'Classic Burger',
  'fries-1': 'French Fries',
  'pizza-1': 'Margherita Pizza',
};

const PRODUCT_ICONS: Record<string, string> = {
  'burger-1': '🍔',
  'fries-1': '🍟',
  'pizza-1': '🍕',
};

export function restaurantName(restaurantId: string): string {
  return RESTAURANTS.find((r) => r.id === restaurantId)?.name ?? restaurantId;
}

export function productName(productId: string, menu?: MenuItem[]): string {
  const fromMenu = menu?.find((m) => m.productId === productId)?.name;
  return fromMenu ?? PRODUCT_NAMES[productId] ?? productId;
}

export function productIcon(productId: string): string {
  return PRODUCT_ICONS[productId] ?? '🍽️';
}

export function formatStatus(status: string): string {
  return status.replace(/_/g, ' ').toLowerCase();
}
