export interface MenuItem {
  productId: string;
  name: string;
  restaurantId: string;
}

export interface OrderLine {
  productId: string;
  quantity: number;
}

export interface PlaceOrderRequest {
  customerId: string;
  restaurantId: string;
  items: OrderLine[];
}

export interface PlaceOrderResponse {
  orderId: string;
  status: string;
  message: string;
}

export interface Order {
  orderId: string;
  customerId: string;
  restaurantId: string;
  status: string;
  items: OrderLine[];
}

export interface LocationUpdate {
  orderId: string;
  latitude: number;
  longitude: number;
  sequence: number;
  delivered: boolean;
}

export interface ApiError {
  message: string;
}

export const RESTAURANTS = [
  { id: 'restaurant-1', name: 'Burger Barn' },
  { id: 'restaurant-2', name: 'Pizza Palace' },
] as const;
