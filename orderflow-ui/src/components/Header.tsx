import { NavLink } from 'react-router-dom';

export default function Header() {
  return (
    <header className="header">
      <div className="header-inner">
        <NavLink to="/" className="logo">
          <div className="logo-icon" aria-hidden>
            🍔
          </div>
          <div>
            <h1>Food Delivery System</h1>
            <span>Browse, order, track</span>
          </div>
        </NavLink>
        <nav className="nav" aria-label="Main">
          <NavLink to="/" className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')} end>
            Order food
          </NavLink>
          <NavLink to="/track" className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}>
            Track order
          </NavLink>
        </nav>
      </div>
    </header>
  );
}
