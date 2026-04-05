// API layer for SeacherController.java endpoints
// All HTTP calls go through here. Components use React Query hooks, never direct fetch.

import type {
  OutputSchema,
  ContentResponse,
  SourceRequest,
  SourceResponse,
  Profile,
} from "@/types/api";

const BASE_URL = "/api/v1";

// POST /api/v1/search — sends JSON string as text/plain body
// Endpoint: SeacherController.search(@RequestBody String request)
export async function search(
  query: string,
  profile?: Profile,
): Promise<OutputSchema | null> {
  const body = profile
    ? JSON.stringify({ prompt: query, perfil: profile })
    : query;

  const response = await fetch(`${BASE_URL}/search`, {
    method: "POST",
    headers: { "Content-Type": "text/plain" },
    body,
  });

  if (response.status === 204) return null;
  if (!response.ok) throw new Error(`Search failed: ${response.status}`);
  return response.json();
}

// GET /api/v1/contents — returns 204 when empty
export async function listContents(): Promise<ContentResponse[]> {
  const response = await fetch(`${BASE_URL}/contents`);
  if (response.status === 204) return [];
  if (!response.ok) throw new Error(`Failed to list contents: ${response.status}`);
  return response.json();
}

// GET /api/v1/contents/{id}
export async function getContent(id: string): Promise<ContentResponse> {
  const response = await fetch(`${BASE_URL}/contents/${id}`);
  if (!response.ok) throw new Error(`Failed to get content: ${response.status}`);
  return response.json();
}

// DELETE /api/v1/contents/{id} — returns 204
export async function deleteContent(id: string): Promise<void> {
  const response = await fetch(`${BASE_URL}/contents/${id}`, {
    method: "DELETE",
  });
  if (!response.ok) throw new Error(`Failed to delete content: ${response.status}`);
}

// POST /api/v1/sources — returns 201 Created
export async function createSource(
  data: SourceRequest,
): Promise<SourceResponse> {
  const response = await fetch(`${BASE_URL}/sources`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
  if (!response.ok) throw new Error(`Failed to create source: ${response.status}`);
  return response.json();
}

// GET /api/v1/sources — returns 204 when empty
export async function listSources(): Promise<SourceResponse[]> {
  const response = await fetch(`${BASE_URL}/sources`);
  if (response.status === 204) return [];
  if (!response.ok) throw new Error(`Failed to list sources: ${response.status}`);
  return response.json();
}

// DELETE /api/v1/sources/{id} — returns 204
export async function deleteSource(id: string): Promise<void> {
  const response = await fetch(`${BASE_URL}/sources/${id}`, {
    method: "DELETE",
  });
  if (!response.ok) throw new Error(`Failed to delete source: ${response.status}`);
}
