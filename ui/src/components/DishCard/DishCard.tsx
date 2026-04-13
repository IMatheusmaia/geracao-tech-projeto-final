import styles from './DishCard.module.css';
import type { Dish } from '../../schemas/dish';

interface DishCardProps {
  dish: Dish;
  onClick: (dish: Dish) => void;
}

export function DishCard({ dish, onClick }: DishCardProps) {
  return (
    <article className={styles.card} onClick={() => onClick(dish)}>
      <div className={styles.imageWrapper}>
        {dish.image_url ? (
          <img src={dish.image_url} alt={dish.title} className={styles.image} />
        ) : (
          <div className={styles.placeholder}>Sem imagem</div>
        )}
      </div>
      <div className={styles.info}>
        <h3 className={styles.title}>{dish.title}</h3>
        <p className={styles.description}>{dish.description}</p>
        <div className={styles.meta}>
          <span className={styles.price}>R$ {dish.price.toFixed(2)}</span>
          <span className={styles.category}>{dish.category}</span>
        </div>
      </div>
    </article>
  );
}
