import { useChatStore } from "@/stores/useChatStore";
import { Button } from "@/components/ui/button";
import type { Profile } from "@/types/api";
import {
  TrendingUp,
  Trophy,
  Heart,
  Gamepad2,
  FlaskConical,
  Banknote,
} from "lucide-react";

const PROFILES: { value: Profile; label: string; icon: React.ReactNode }[] = [
  { value: "economia", label: "Economia", icon: <TrendingUp className="h-4 w-4" /> },
  { value: "esporte", label: "Esporte", icon: <Trophy className="h-4 w-4" /> },
  { value: "saude", label: "Saude", icon: <Heart className="h-4 w-4" /> },
  { value: "lazer", label: "Lazer", icon: <Gamepad2 className="h-4 w-4" /> },
  { value: "ciencia", label: "Ciencia", icon: <FlaskConical className="h-4 w-4" /> },
  { value: "financas", label: "Financas", icon: <Banknote className="h-4 w-4" /> },
];

export function ProfileToggle() {
  const { activeProfile, setActiveProfile } = useChatStore();

  function handleToggle(profile: Profile) {
    setActiveProfile(activeProfile === profile ? null : profile);
  }

  return (
    <div className="flex flex-wrap gap-2 justify-center pb-2">
      {PROFILES.map(({ value, label, icon }) => (
        <Button
          key={value}
          variant={activeProfile === value ? "default" : "outline"}
          size="sm"
          className="gap-1.5"
          onClick={() => handleToggle(value)}
        >
          {icon}
          {label}
        </Button>
      ))}
    </div>
  );
}
