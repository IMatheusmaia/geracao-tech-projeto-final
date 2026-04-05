import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { listContents, getContent, deleteContent } from "@/api/searcher";

// GET /api/v1/contents
export function useContents() {
  return useQuery({
    queryKey: ["contents"],
    queryFn: listContents,
    staleTime: 30_000,
  });
}

// GET /api/v1/contents/{id}
export function useContent(id: string | null) {
  return useQuery({
    queryKey: ["contents", id],
    queryFn: () => getContent(id!),
    enabled: !!id,
  });
}

// DELETE /api/v1/contents/{id}
export function useDeleteContent() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: deleteContent,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["contents"] });
    },
  });
}
