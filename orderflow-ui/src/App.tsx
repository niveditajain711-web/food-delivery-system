import { Route, Routes } from 'react-router-dom';
import Header from './components/Header';
import NewOrderPage from './pages/NewOrderPage';
import OrderDetailPage from './pages/OrderDetailPage';
import TrackOrderPage from './pages/TrackOrderPage';

export default function App() {
  return (
    <div className="app">
      <Header />
      <main className="main">
        <Routes>
          <Route path="/" element={<NewOrderPage />} />
          <Route path="/track" element={<TrackOrderPage />} />
          <Route path="/orders/:orderId" element={<OrderDetailPage />} />
        </Routes>
      </main>
      <footer className="footer">
        <p>Food Delivery System — React · REST gateway · gRPC microservices</p>
      </footer>
    </div>
  );
}
