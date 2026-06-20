export type OrderStatus = 'Pending' | 'Processing' | 'Shipped' | 'Delivered' | 'Cancelled';

export interface ShippingAddress {
  street: string;
  houseNo: string;
  city: string;
  zipCode: string; // Aligned with CustomerProfile naming convention
  country: string;
}

export interface OrderItem {
  productId: string;
  productName: string;
  quantity: number;
  unitPrice: number;
  lineTotal: number;
}

export interface CustomerOrder {
  orderId: string;
  orderNumber: string;
  orderDate: string;
  status: OrderStatus;

  customerId: string;
  customerName: string;
  customerEmail: string;

  shippingAddress: ShippingAddress;
  items: OrderItem[];

  subtotal: number;
  grandTotal: number;
  trackingNumber?: string;

  createdAt: string;
  updatedAt: string;
  createdBy: string;
}
