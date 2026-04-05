import { Trash2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";
import type { ContentResponse } from "@/types/api";

interface ConversationItemProps {
  content: ContentResponse;
  isSelected: boolean;
  onClick: (id: string) => void;
  onDelete: (id: string) => void;
}

export function ConversationItem({
  content,
  isSelected,
  onClick,
  onDelete,
}: ConversationItemProps) {
  return (
    <div
      className={cn(
        "group flex items-center gap-2 px-3 py-2.5 rounded-lg cursor-pointer transition-colors",
        isSelected
          ? "bg-accent text-accent-foreground"
          : "hover:bg-accent/50 text-muted-foreground hover:text-foreground",
      )}
      onClick={() => onClick(content.id)}
    >
      <div className="flex-1 min-w-0">
        <p className="text-sm truncate">{content.title}</p>
        <p className="text-xs text-muted-foreground mt-0.5">
          {content.savedAt}
        </p>
      </div>
      <Button
        variant="ghost"
        size="icon"
        className="h-7 w-7 opacity-0 group-hover:opacity-100 shrink-0"
        onClick={(e) => {
          e.stopPropagation();
          onDelete(content.id);
        }}
      >
        <Trash2 className="h-3.5 w-3.5 text-destructive" />
      </Button>
    </div>
  );
}
