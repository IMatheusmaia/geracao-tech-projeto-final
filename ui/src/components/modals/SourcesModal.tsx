import { useForm } from "react-hook-form";
import { z } from "zod/v4";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Separator } from "@/components/ui/separator";
import { Skeleton } from "@/components/ui/skeleton";
import { Trash2, Plus, ExternalLink } from "lucide-react";
import { useChatStore } from "@/stores/useChatStore";
import { useSources, useCreateSource, useDeleteSource } from "@/hooks/useSources";

const sourceSchema = z.object({
  name: z.string().min(1, "Nome e obrigatorio").max(100),
  baseUrl: z.string().url("URL invalida"),
});

type SourceForm = z.infer<typeof sourceSchema>;

export function SourcesModal() {
  const { isSourcesModalOpen, setSourcesModalOpen } = useChatStore();
  const { data: sources, isLoading } = useSources();
  const createSource = useCreateSource();
  const deleteSource = useDeleteSource();

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<SourceForm>({
    resolver: zodResolver(sourceSchema),
  });

  function onSubmit(data: SourceForm) {
    createSource.mutate(data, {
      onSuccess: () => reset(),
    });
  }

  return (
    <Dialog open={isSourcesModalOpen} onOpenChange={setSourcesModalOpen}>
      <DialogContent className="max-w-lg max-h-[80vh]">
        <DialogHeader>
          <DialogTitle>Fontes de Pesquisa</DialogTitle>
        </DialogHeader>

        {/* Create form */}
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-3">
          <div className="space-y-1.5">
            <Input
              {...register("name")}
              placeholder="Nome da fonte"
              className="bg-secondary border-border"
            />
            {errors.name && (
              <p className="text-destructive text-xs">{errors.name.message}</p>
            )}
          </div>
          <div className="space-y-1.5">
            <Input
              {...register("baseUrl")}
              placeholder="https://www.exemplo.com"
              className="bg-secondary border-border"
            />
            {errors.baseUrl && (
              <p className="text-destructive text-xs">
                {errors.baseUrl.message}
              </p>
            )}
          </div>
          <Button
            type="submit"
            size="sm"
            className="w-full"
            disabled={createSource.isPending}
          >
            <Plus className="h-4 w-4 mr-1" />
            Adicionar Fonte
          </Button>
        </form>

        <Separator />

        {/* Sources list */}
        <ScrollArea className="max-h-[300px]">
          {isLoading ? (
            <div className="space-y-2">
              {Array.from({ length: 2 }).map((_, i) => (
                <Skeleton key={i} className="h-14 w-full" />
              ))}
            </div>
          ) : !sources || sources.length === 0 ? (
            <p className="text-sm text-muted-foreground text-center py-4">
              Nenhuma fonte cadastrada
            </p>
          ) : (
            <div className="space-y-2">
              {sources.map((source) => (
                <div
                  key={source.id}
                  className="flex items-center gap-3 p-2.5 rounded-lg bg-secondary group"
                >
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium truncate">
                      {source.name}
                    </p>
                    <a
                      href={source.baseUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="text-xs text-muted-foreground hover:text-primary flex items-center gap-1"
                    >
                      {source.baseUrl}
                      <ExternalLink className="h-3 w-3" />
                    </a>
                  </div>
                  <Button
                    variant="ghost"
                    size="icon"
                    className="h-7 w-7 shrink-0"
                    disabled={deleteSource.isPending}
                    onClick={() => deleteSource.mutate(source.id)}
                  >
                    <Trash2 className="h-3.5 w-3.5 text-destructive" />
                  </Button>
                </div>
              ))}
            </div>
          )}
        </ScrollArea>
      </DialogContent>
    </Dialog>
  );
}
