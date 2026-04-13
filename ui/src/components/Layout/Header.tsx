import { Link, useNavigate } from '@tanstack/react-router';
import { useAuth } from '../../hooks/useAuth';
import styles from './Header.module.css';

export function Header() {
  const { isAuthenticated, isAdmin, name, logout } = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    void navigate({ to: '/' });
  }

  return (
    <header className={styles.header}>
      <div className={styles.inner}>
        <Link to="/" className={styles.logo}>
          Smart Menu
        </Link>
        <nav className={styles.nav}>
          <Link to="/">Cardápio</Link>
          {isAuthenticated && isAdmin && <Link to="/admin">Admin</Link>}
          {isAuthenticated ? (
            <div className={styles.userSection}>
              <span>Olá, {name}</span>
              <button className={styles.logoutBtn} onClick={handleLogout}>
                Sair
              </button>
            </div>
          ) : (
            <Link to="/login">Entrar</Link>
          )}
        </nav>
      </div>
    </header>
  );
}
