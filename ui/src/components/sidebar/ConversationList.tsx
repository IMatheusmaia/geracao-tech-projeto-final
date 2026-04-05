import { RefreshCw } from "lucide-react";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Skeleton } from "@/components/ui/skeleton";
import { Button } from "@/components/ui/button";
import { ConversationItem } from "./ConversationItem";
import { useContents, useDeleteContent } from "@/hooks/useContents";
import { useChatStore } from "@/stores/useChatStore";

export function ConversationList() {
  const { data: contents, isLoading, refetch } = useContents();
  const deleteContent = useDeleteContent();
  const { selectedContentId, setSelectedContentId, openModal } = useChatStore();

  function handleClick(id: string) {
    const content = contents?.find((c) => c.id === id);
    if (content) {
      setSelectedContentId(id);
      openModal({
        type: "content",
        title: content.title,
        content: content.content,
      });
    }
  }

  function handleDelete(id: string) {
    deleteContent.mutate(id);
  }

  return (
    <div className="flex flex-col h-full">
      <div className="flex items-center justify-between px-3 py-2">
        <h3 className="text-xs font-semibold text-muted-foreground uppercase tracking-wider">
          Conteudos Salvos
        </h3>
        <Button
          variant="ghost"
          size="icon"
          className="h-6 w-6"
          onClick={() => refetch()}
        >
          <RefreshCw className="h-3.5 w-3.5" />
        </Button>
      </div>

      <ScrollArea className="flex-1">
        {isLoading ? (
          <div className="space-y-2 px-3">
            {Array.from({ length: 3 }).map((_, i) => (
              <Skeleton key={i} className="h-12 w-full" />
            ))}
          </div>
        ) : !contents || contents.length === 0 ? (
          <p className="text-xs text-muted-foreground px-3 py-4 text-center">
            Nenhum conteudo salvo
          </p>
        ) : (
          <div className="space-y-1 px-2">
            {contents.map((content) => (
              <ConversationItem
                key={content.id}
                content={content}
                isSelected={selectedContentId === content.id}
                onClick={handleClick}
                onDelete={handleDelete}
              />
            ))}
          </div>
        )}
      </ScrollArea>
    </div>
  );
}
