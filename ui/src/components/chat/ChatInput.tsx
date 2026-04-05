import { useState, useRef, useCallback } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod/v4";
import { zodResolver } from "@hookform/resolvers/zod";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";
import { Send } from "lucide-react";
import { useSearch } from "@/hooks/useSearch";
import { useChatStore } from "@/stores/useChatStore";

const searchSchema = z.object({
  query: z.string().min(1, "Digite uma mensagem antes de enviar"),
});

type SearchForm = z.infer<typeof searchSchema>;

export function ChatInput() {
  const [localValue, setLocalValue] = useState("");
  const textareaRef = useRef<HTMLTextAreaElement>(null);
  const searchMutation = useSearch();
  const { activeProfile, addMessage, setLoading } = useChatStore();

  const {
    formState: { errors },
    setValue,
    handleSubmit,
    clearErrors,
  } = useForm<SearchForm>({
    resolver: zodResolver(searchSchema),
    defaultValues: { query: "" },
  });

  const onSubmit = useCallback(
    (data: SearchForm) => {
      addMessage({
        id: crypto.randomUUID(),
        role: "user",
        content: data.query,
        timestamp: new Date(),
      });
      setLoading(true);
      searchMutation.mutate(
        { query: data.query, profile: activeProfile ?? undefined },
        { onSettled: () => setLoading(false) },
      );
      setLocalValue("");
      setValue("query", "");
      clearErrors();
    },
    [addMessage, activeProfile, searchMutation, setLoading, setValue, clearErrors],
  );

  function handleKeyDown(e: React.KeyboardEvent<HTMLTextAreaElement>) {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      if (localValue.trim()) {
        setValue("query", localValue.trim());
        handleSubmit(onSubmit)();
      }
    }
  }

  return (
    <div className="max-w-3xl mx-auto w-full px-4 pb-4">
      <form onSubmit={handleSubmit(onSubmit)} className="relative">
        <Textarea
          ref={textareaRef}
          value={localValue}
          onChange={(e) => {
            setLocalValue(e.target.value);
            setValue("query", e.target.value);
            clearErrors("query");
          }}
          onKeyDown={handleKeyDown}
          placeholder="Digite sua busca..."
          className="resize-none pr-12 min-h-[52px] max-h-[200px] bg-secondary border-border text-foreground placeholder:text-muted-foreground"
          rows={1}
          disabled={searchMutation.isPending}
        />
        <Button
          type="submit"
          size="icon"
          className="absolute right-2 bottom-2 h-8 w-8"
          disabled={searchMutation.isPending || !localValue.trim()}
        >
          <Send className="h-4 w-4" />
        </Button>
      </form>
      {errors.query && (
        <p className="text-destructive text-xs mt-1 px-1">
          {errors.query.message}
        </p>
      )}
    </div>
  );
}
