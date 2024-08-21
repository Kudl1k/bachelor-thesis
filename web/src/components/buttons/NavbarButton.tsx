import { Link } from "react-router-dom";
import { buttonVariants } from "../ui/button";
import { LucideIcon } from "lucide-react"; // Import the type for Lucide icons

interface NavBarButtonProps {
    text: string;
    active?: boolean;
    link: string;
    icon: LucideIcon; // Update the type to LucideIcon
    onClick: () => void;
}

export function NavBarButton({ text, active, link, icon: Icon, onClick }: NavBarButtonProps) {
    return (
        <Link 
            className={`${buttonVariants({ variant: "link", size: "default" })} nav-button ${active ? "font-bold shadow-md" : "font-normal"} m-2`} 
            onClick={onClick} 
            to={link}
        >
            <Icon className="mr-2" /> {/* Render the icon */}
            {text}
        </Link>
    );
}