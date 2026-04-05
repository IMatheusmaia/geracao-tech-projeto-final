import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { listSources, createSource, deleteSource } from "@/api/searcher";
import type { SourceRequest } from "@/types/api";

// GET /api/v1/sources
export function useSources() {
  return useQuery({
    queryKey: ["sources"],
    queryFn: listSources,
    staleTime: 60_000,
  });
}

// POST /api/v1/sources
export function useCreateSource() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: SourceRequest) => createSource(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["sources"] });
    },
  });
}

// DELETE /api/v1/sources/{id}
export function useDeleteSource() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: deleteSource,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["sources"] });
    },
  });
}
