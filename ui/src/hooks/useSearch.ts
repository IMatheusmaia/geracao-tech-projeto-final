import { useMutation } from "@tanstack/react-query";
import { useChatStore } from "@/stores/useChatStore";
import { search } from "@/api/searcher";
import type { Profile } from "@/types/api";

// Handles all 4 response scenarios from POST /search:
// A: messages only -> render in chat
// B: messages + searched_data -> render in chat + open modal
// C: messages + retriveled_data -> render in chat + open modal
// D: all null -> show fallback error message
export function useSearch() {
  const { addMessage, openModal } = useChatStore();

  return useMutation({
    mutationFn: ({
      query,
      profile,
    }: {
      query: string;
      profile?: Profile;
    }) => search(query, profile),

    onSuccess: (data) => {
      if (!data) {
        // Scenario D: 204 No Content or null
        addMessage({
          id: crypto.randomUUID(),
          role: "assistant",
          content:
            "Nao foi possivel obter uma resposta. Tente reformular sua busca.",
          timestamp: new Date(),
        });
        return;
      }

      const assistantContent =
        data.messages?.[0]?.content ?? "Sem resposta disponivel.";

      addMessage({
        id: crypto.randomUUID(),
        role: "assistant",
        content: assistantContent,
        timestamp: new Date(),
        retriveledData: data.retriveled_data ?? undefined,
        searchedData: data.searched_data ?? undefined,
      });

      // Scenario B: open modal with searched_data
      if (data.searched_data) {
        openModal({
          type: "searched",
          title: data.searched_data.title,
          content: data.searched_data.content,
        });
      }
      // Scenario C: open modal with retriveled_data
      else if (data.retriveled_data) {
        openModal({
          type: "retrieved",
          title: data.retriveled_data.title,
          content: data.retriveled_data.content,
        });
      }
      // Scenario A: messages only, no modal needed
    },

    onError: (error) => {
      addMessage({
        id: crypto.randomUUID(),
        role: "assistant",
        content: `Erro ao buscar: ${error.message}`,
        timestamp: new Date(),
      });
    },
  });
}
