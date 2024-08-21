import { LucideIcon } from "lucide-react"; // Import the type for Lucide icons

import {
  Alert,
  AlertDescription,
  AlertTitle,
} from "@/components/ui/alert"

interface InfoBoxProps {
    alertTitle: string;
    alertDescription: string;
    icon: LucideIcon;
}

export function InfoBox({ alertTitle, alertDescription, icon: Icon }: InfoBoxProps) {
  return (
    <Alert>
        <Icon className="h-4 w-4" />
      <AlertTitle>{alertTitle}</AlertTitle>
      <AlertDescription>
        {alertDescription}
      </AlertDescription>
    </Alert>
  )
}
