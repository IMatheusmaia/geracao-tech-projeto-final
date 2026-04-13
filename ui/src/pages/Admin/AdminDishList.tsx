import { useState } from 'react';
import {
  useDishes,
  useDeleteDish,
  useCreateDish,
  useUpdateDish,
} from '../../hooks/useDishes';
import { DishModal } from '../../components/DishModal/DishModal';
import { ConfirmDialog } from '../../components/ConfirmDialog/ConfirmDialog';
import { Spinner } from '../../components/Spinner/Spinner';
import type { Dish, DishFormData } from '../../schemas/dish';
import styles from './AdminDishList.module.css';

export function AdminDishList() {
  const { data: dishes, isLoading, error } = useDishes();
  const deleteDish = useDeleteDish();
  const createDish = useCreateDish();
  const updateDish = useUpdateDish();

  const [editingDish, setEditingDish] = useState<Dish | null>(null);
  const [isCreating, setIsCreating] = useState(false);
  const [deletingDish, setDeletingDish] = useState<Dish | null>(null);

  const isSaving =
    createDish.isPending || updateDish.isPending;

  function handleSave(data: DishFormData) {
    if (isCreating) {
      createDish.mutate(data, {
        onSuccess: () => setIsCreating(false),
      });
    } else if (editingDish) {
      updateDish.mutate(
        { id: editingDish.id, data },
        { onSuccess: () => setEditingDish(null) },
      );
    }
  }

  function handleDelete() {
    if (!deletingDish) return;
    deleteDish.mutate(deletingDish.id, {
      onSuccess: () => setDeletingDish(null),
    });
  }

  if (isLoading) return <Spinner />;
  if (error)
    return <p className={styles.error}>Erro ao carregar pratos.</p>;

  return (
    <div>
      <div className={styles.toolbar}>
        <button className={styles.newBtn} onClick={() => setIsCreating(true)}>
          Novo Prato
        </button>
      </div>

      {!dishes?.length ? (
        <p className={styles.empty}>Nenhum prato cadastrado.</p>
      ) : (
        <div className={styles.list}>
          {dishes.map((dish) => (
            <div key={dish.id} className={styles.row}>
              {dish.image_url ? (
                <img
                  src={dish.image_url}
                  alt={dish.title}
                  className={styles.rowImage}
                />
              ) : (
                <div className={styles.rowImagePlaceholder}>N/A</div>
              )}
              <div className={styles.rowInfo}>
                <div className={styles.rowTitle}>{dish.title}</div>
                <div className={styles.rowMeta}>
                  <span>{dish.category}</span>
                  <span>R$ {dish.price.toFixed(2)}</span>
                </div>
              </div>
              <div className={styles.rowActions}>
                <button
                  className={styles.editBtn}
                  onClick={() => setEditingDish(dish)}
                >
                  Editar
                </button>
                <button
                  className={styles.deleteBtn}
                  onClick={() => setDeletingDish(dish)}
                >
                  Excluir
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {(isCreating || editingDish) && (
        <DishModal
          dish={editingDish}
          mode={isCreating ? 'create' : 'edit'}
          isOpen={true}
          onClose={() => {
            setIsCreating(false);
            setEditingDish(null);
          }}
          onSave={handleSave}
          isSaving={isSaving}
        />
      )}

      {deletingDish && (
        <ConfirmDialog
          message={`Tem certeza que deseja excluir "${deletingDish.title}"?`}
          onConfirm={handleDelete}
          onCancel={() => setDeletingDish(null)}
        />
      )}
    </div>
  );
}
