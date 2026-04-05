import { create } from "zustand";

interface SourceState {
  isFormOpen: boolean;
  setFormOpen: (open: boolean) => void;
}

export const useSourceStore = create<SourceState>((set) => ({
  isFormOpen: false,
  setFormOpen: (open) => set({ isFormOpen: open }),
}));
