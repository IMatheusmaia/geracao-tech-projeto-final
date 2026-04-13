import styles from './Footer.module.css';

export function Footer() {
  return (
    <footer className={styles.footer}>
      Smart Menu &copy; {new Date().getFullYear()}
    </footer>
  );
}
