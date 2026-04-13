import { useState } from 'react';
import { useForm } from '@tanstack/react-form';
import styles from './DishModal.module.css';
import { ImageCarousel } from '../ImageCarousel/ImageCarousel';
import { Spinner } from '../Spinner/Spinner';
import {
  useDishImageSearch,
  useUpdateDishImage,
} from '../../hooks/useDishes';
import {
  dishFormSchema,
  type Dish,
  type DishFormData,
} from '../../schemas/dish';

interface DishModalProps {
  dish: Dish | null;
  mode: 'view' | 'edit' | 'create';
  isOpen: boolean;
  onClose: () => void;
  onSave: (data: DishFormData) => void;
  isSaving?: boolean;
}

export function DishModal({
  dish,
  mode,
  isOpen,
  onClose,
  onSave,
  isSaving = false,
}: DishModalProps) {
  if (!isOpen) return null;

  if (mode === 'view' && dish) {
    return <ViewMode dish={dish} onClose={onClose} />;
  }

  return (
    <FormMode
      dish={dish}
      mode={mode as 'edit' | 'create'}
      onClose={onClose}
      onSave={onSave}
      isSaving={isSaving}
    />
  );
}

function ViewMode({ dish, onClose }: { dish: Dish; onClose: () => void }) {
  return (
    <div className={styles.overlay} onClick={onClose}>
      <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
        <div className={styles.header}>
          <h2>{dish.title}</h2>
          <button className={styles.closeBtn} onClick={onClose}>
            &times;
          </button>
        </div>
        <div className={styles.body}>
          {dish.image_url ? (
            <img
              src={dish.image_url}
              alt={dish.title}
              className={styles.dishImage}
            />
          ) : (
            <div className={styles.dishImagePlaceholder}>Sem imagem</div>
          )}
          <h3 className={styles.dishTitle}>{dish.title}</h3>
          <p className={styles.dishDescription}>{dish.description}</p>
          <div className={styles.dishMeta}>
            <span className={styles.dishPrice}>
              R$ {dish.price.toFixed(2)}
            </span>
            <span className={styles.dishCategory}>{dish.category}</span>
          </div>
          <h4 className={styles.ingredientsTitle}>Ingredientes</h4>
          <ul className={styles.ingredientsList}>
            {dish.ingredients.map((ing, i) => (
              <li key={i} className={styles.ingredientTag}>
                {ing}
              </li>
            ))}
          </ul>
          <div className={styles.dishDates}>
            {dish.saved_at && (
              <span>
                Criado:{' '}
                {new Date(dish.saved_at).toLocaleDateString('pt-BR')}
              </span>
            )}
            {dish.updated_at && (
              <span>
                Atualizado:{' '}
                {new Date(dish.updated_at).toLocaleDateString('pt-BR')}
              </span>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

interface FormModeProps {
  dish: Dish | null;
  mode: 'edit' | 'create';
  onClose: () => void;
  onSave: (data: DishFormData) => void;
  isSaving: boolean;
}

function FormMode({ dish, mode, onClose, onSave, isSaving }: FormModeProps) {
  const [ingredientInput, setIngredientInput] = useState('');
  const [isSearchOpen, setIsSearchOpen] = useState(false);
  const [selectedImageUrl, setSelectedImageUrl] = useState<string | null>(
    null,
  );
  const [formErrors, setFormErrors] = useState<Record<string, string>>({});

  const imageSearch = useDishImageSearch(dish?.id ?? '');
  const updateImageMutation = useUpdateDishImage();

  const form = useForm({
    defaultValues: {
      title: dish?.title ?? '',
      description: dish?.description ?? '',
      category: dish?.category ?? '',
      price: dish?.price ?? 0,
      imageUrl: dish?.image_url ?? '',
      ingredients: dish?.ingredients ?? [],
    },
    onSubmit: ({ value }) => {
      const result = dishFormSchema.safeParse(value);
      if (!result.success) {
        const errors: Record<string, string> = {};
        for (const issue of result.error.issues) {
          const key = issue.path[0]?.toString();
          if (key && !errors[key]) {
            errors[key] = issue.message;
          }
        }
        setFormErrors(errors);
        return;
      }
      setFormErrors({});
      onSave(result.data);
    },
  });

  function handleAddIngredient() {
    const val = ingredientInput.trim();
    if (!val) return;
    form.setFieldValue('ingredients', [
      ...(form.state.values.ingredients ?? []),
      val,
    ]);
    setIngredientInput('');
  }

  function handleRemoveIngredient(index: number) {
    const current = form.state.values.ingredients ?? [];
    form.setFieldValue(
      'ingredients',
      current.filter((_, i) => i !== index),
    );
  }

  function handleIngredientKeyDown(
    e: React.KeyboardEvent<HTMLInputElement>,
  ) {
    if (e.key === 'Enter') {
      e.preventDefault();
      handleAddIngredient();
    }
  }

  async function handleImageSearch() {
    if (!dish?.id) return;
    setIsSearchOpen(true);
    await imageSearch.refetch();
  }

  function handleConfirmImage() {
    if (!selectedImageUrl || !dish?.id) return;
    updateImageMutation.mutate(
      { id: dish.id, imageUrl: selectedImageUrl },
      {
        onSuccess: (updatedDish) => {
          form.setFieldValue('imageUrl', updatedDish.image_url ?? '');
          setIsSearchOpen(false);
          setSelectedImageUrl(null);
        },
      },
    );
  }

  function clearFieldError(name: string) {
    setFormErrors((prev) => {
      const next = { ...prev };
      delete next[name];
      return next;
    });
  }

  return (
    <div className={styles.overlay} onClick={onClose}>
      <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
        <div className={styles.header}>
          <h2>{mode === 'create' ? 'Novo Prato' : 'Editar Prato'}</h2>
          <button className={styles.closeBtn} onClick={onClose}>
            &times;
          </button>
        </div>
        <div className={styles.body}>
          <form
            onSubmit={(e) => {
              e.preventDefault();
              void form.handleSubmit();
            }}
          >
            <form.Field name="title">
              {(field) => (
                <div className={styles.formGroup}>
                  <label htmlFor="title">Título</label>
                  <input
                    id="title"
                    type="text"
                    value={field.state.value}
                    onBlur={field.handleBlur}
                    onChange={(e) => {
                      field.handleChange(e.target.value);
                      clearFieldError('title');
                    }}
                  />
                  {formErrors.title && (
                    <p className={styles.fieldError}>{formErrors.title}</p>
                  )}
                </div>
              )}
            </form.Field>

            <form.Field name="description">
              {(field) => (
                <div className={styles.formGroup}>
                  <label htmlFor="description">Descrição</label>
                  <textarea
                    id="description"
                    value={field.state.value}
                    onBlur={field.handleBlur}
                    onChange={(e) => {
                      field.handleChange(e.target.value);
                      clearFieldError('description');
                    }}
                  />
                  {formErrors.description && (
                    <p className={styles.fieldError}>
                      {formErrors.description}
                    </p>
                  )}
                </div>
              )}
            </form.Field>

            <form.Field name="category">
              {(field) => (
                <div className={styles.formGroup}>
                  <label htmlFor="category">Categoria</label>
                  <input
                    id="category"
                    type="text"
                    value={field.state.value}
                    onBlur={field.handleBlur}
                    onChange={(e) => {
                      field.handleChange(e.target.value);
                      clearFieldError('category');
                    }}
                  />
                  {formErrors.category && (
                    <p className={styles.fieldError}>{formErrors.category}</p>
                  )}
                </div>
              )}
            </form.Field>

            <form.Field name="price">
              {(field) => (
                <div className={styles.formGroup}>
                  <label htmlFor="price">Preço</label>
                  <input
                    id="price"
                    type="number"
                    step="0.01"
                    min="0"
                    value={field.state.value}
                    onBlur={field.handleBlur}
                    onChange={(e) => {
                      field.handleChange(
                        e.target.value === '' ? 0 : Number(e.target.value),
                      );
                      clearFieldError('price');
                    }}
                  />
                  {formErrors.price && (
                    <p className={styles.fieldError}>{formErrors.price}</p>
                  )}
                </div>
              )}
            </form.Field>

            <form.Field name="imageUrl">
              {(field) => (
                <div className={styles.formGroup}>
                  <label htmlFor="imageUrl">URL da Imagem</label>
                  <input
                    id="imageUrl"
                    type="text"
                    value={field.state.value}
                    onBlur={field.handleBlur}
                    onChange={(e) => {
                      field.handleChange(e.target.value);
                      clearFieldError('imageUrl');
                    }}
                  />
                  {formErrors.imageUrl && (
                    <p className={styles.fieldError}>{formErrors.imageUrl}</p>
                  )}

                  {mode === 'edit' && dish?.id && (
                    <div className={styles.imageSearchSection}>
                      <button
                        type="button"
                        className={styles.searchBtn}
                        onClick={handleImageSearch}
                        disabled={imageSearch.isFetching}
                      >
                        {imageSearch.isFetching
                          ? 'Buscando...'
                          : 'Buscar imagem com IA'}
                      </button>

                      {isSearchOpen && imageSearch.isFetching && <Spinner />}

                      {isSearchOpen &&
                        imageSearch.data &&
                        !imageSearch.isFetching && (
                          <>
                            <ImageCarousel
                              results={imageSearch.data}
                              selectedUrl={selectedImageUrl}
                              onSelect={setSelectedImageUrl}
                            />
                            <button
                              type="button"
                              className={styles.confirmImageBtn}
                              onClick={handleConfirmImage}
                              disabled={
                                !selectedImageUrl ||
                                updateImageMutation.isPending
                              }
                            >
                              {updateImageMutation.isPending
                                ? 'Salvando...'
                                : 'Usar esta imagem'}
                            </button>
                          </>
                        )}
                    </div>
                  )}
                </div>
              )}
            </form.Field>

            <form.Field name="ingredients">
              {(field) => (
                <div className={styles.formGroup}>
                  <label>Ingredientes</label>
                  <div className={styles.tagInput}>
                    <input
                      type="text"
                      value={ingredientInput}
                      onChange={(e) => setIngredientInput(e.target.value)}
                      onKeyDown={handleIngredientKeyDown}
                      placeholder="Digite e pressione Enter"
                    />
                    <button type="button" onClick={handleAddIngredient}>
                      Adicionar
                    </button>
                  </div>
                  <div className={styles.tagsContainer}>
                    {(field.state.value ?? []).map((ing, i) => (
                      <span key={i} className={styles.tag}>
                        {ing}
                        <button
                          type="button"
                          onClick={() => handleRemoveIngredient(i)}
                        >
                          &times;
                        </button>
                      </span>
                    ))}
                  </div>
                  {formErrors.ingredients && (
                    <p className={styles.fieldError}>
                      {formErrors.ingredients}
                    </p>
                  )}
                </div>
              )}
            </form.Field>

            <div className={styles.formActions}>
              <button
                type="button"
                className={styles.secondaryBtn}
                onClick={onClose}
              >
                Cancelar
              </button>
              <button
                type="submit"
                className={styles.primaryBtn}
                disabled={isSaving}
              >
                {isSaving ? 'Salvando...' : 'Salvar'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
