import styles from './ImageCarousel.module.css';
import type { ImageSearchResult } from '../../schemas/dish';

interface ImageCarouselProps {
  results: ImageSearchResult;
  selectedUrl: string | null;
  onSelect: (url: string) => void;
}

export function ImageCarousel({
  results,
  selectedUrl,
  onSelect,
}: ImageCarouselProps) {
  if (results.images.length === 0) {
    return <p className={styles.empty}>Nenhuma imagem encontrada.</p>;
  }

  return (
    <div className={styles.carousel}>
      {results.images.map((img, idx) => (
        <button
          key={idx}
          className={`${styles.thumbnail} ${selectedUrl === img.image_url ? styles.selected : ''}`}
          onClick={() => onSelect(img.image_url)}
          type="button"
        >
          <img src={img.image_url} alt={`Resultado ${idx + 1}`} />
        </button>
      ))}
    </div>
  );
}
