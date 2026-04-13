import styles from './CategoryTabs.module.css';

interface CategoryTabsProps {
  categories: string[];
  selected: string;
  onSelect: (category: string) => void;
}

export function CategoryTabs({
  categories,
  selected,
  onSelect,
}: CategoryTabsProps) {
  const allTabs = ['Todos', ...categories];
  return (
    <div className={styles.tabs}>
      {allTabs.map((tab) => (
        <button
          key={tab}
          className={`${styles.tab} ${selected === tab ? styles.active : ''}`}
          onClick={() => onSelect(tab)}
        >
          {tab}
        </button>
      ))}
    </div>
  );
}
