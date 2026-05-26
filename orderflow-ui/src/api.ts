import type {
  ApiError,
  LocationUpdate,
  MenuItem,
  Order,
  PlaceOrderRequest,
  PlaceOrderResponse,
} from './types';

const API_BASE = '/api';

async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const body = (await response.json().catch(() => ({}))) as ApiError;
    throw new Error(body.message ?? `Request failed (${response.status})`);
  }
  return response.json() as Promise<T>;
}

export async function fetchMenu(restaurantId?: string): Promise<MenuItem[]> {
  const query = restaurantId ? `?restaurantId=${encodeURIComponent(restaurantId)}` : '';
  const response = await fetch(`${API_BASE}/menu${query}`);
  return handleResponse<MenuItem[]>(response);
}

export async function placeOrder(request: PlaceOrderRequest): Promise<PlaceOrderResponse> {
  const response = await fetch(`${API_BASE}/orders`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request),
  });
  return handleResponse<PlaceOrderResponse>(response);
}

export async function fetchOrder(orderId: string): Promise<Order> {
  const response = await fetch(`${API_BASE}/orders/${encodeURIComponent(orderId)}`);
  return handleResponse<Order>(response);
}

export function subscribeToTracking(
  orderId: string,
  onUpdate: (update: LocationUpdate) => void,
  onDone: () => void,
  onError: (message: string) => void
): () => void {
  const source = new EventSource(`${API_BASE}/orders/${encodeURIComponent(orderId)}/track`);

  source.addEventListener('location', (event) => {
    try {
      const update = JSON.parse((event as MessageEvent).data) as LocationUpdate;
      onUpdate(update);
      if (update.delivered) {
        source.close();
        onDone();
      }
    } catch {
      onError('Failed to parse tracking update');
    }
  });

  source.onerror = () => {
    if (source.readyState === EventSource.CLOSED) {
      return;
    }
    source.close();
    onError('Tracking connection lost');
  };

  return () => source.close();
}
