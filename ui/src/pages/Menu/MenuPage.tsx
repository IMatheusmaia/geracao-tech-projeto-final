import { useState, useMemo } from 'react';
import { useDishes } from '../../hooks/useDishes';
import { CategoryTabs } from '../../components/CategoryTabs/CategoryTabs';
import { DishCard } from '../../components/DishCard/DishCard';
import { DishModal } from '../../components/DishModal/DishModal';
import { Spinner } from '../../components/Spinner/Spinner';
import type { Dish } from '../../schemas/dish';
import styles from './MenuPage.module.css';

export function MenuPage() {
  const { data: dishes, isLoading, error } = useDishes();
  const [selectedCategory, setSelectedCategory] = useState('Todos');
  const [selectedDish, setSelectedDish] = useState<Dish | null>(null);

  const categories = useMemo(() => {
    if (!dishes) return [];
    const cats = [...new Set(dishes.map((d) => d.category))];
    return cats.sort((a, b) => a.localeCompare(b));
  }, [dishes]);

  const filteredDishes = useMemo(() => {
    if (!dishes) return [];
    const filtered =
      selectedCategory === 'Todos'
        ? dishes
        : dishes.filter((d) => d.category === selectedCategory);
    return filtered.sort((a, b) => a.title.localeCompare(b.title));
  }, [dishes, selectedCategory]);

  if (isLoading) return <Spinner />;
  if (error)
    return <p className={styles.error}>Erro ao carregar cardápio.</p>;
  if (!dishes?.length)
    return <p className={styles.empty}>Nenhum prato disponível.</p>;

  return (
    <div className={styles.page}>
      <h1>Cardápio</h1>
      <CategoryTabs
        categories={categories}
        selected={selectedCategory}
        onSelect={setSelectedCategory}
      />
      <div className={styles.grid}>
        {filteredDishes.map((dish) => (
          <DishCard key={dish.id} dish={dish} onClick={setSelectedDish} />
        ))}
      </div>
      <DishModal
        dish={selectedDish}
        mode="view"
        isOpen={selectedDish !== null}
        onClose={() => setSelectedDish(null)}
        onSave={() => {}}
      />
    </div>
  );
}
