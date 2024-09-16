import { ThemeProvider } from "@/components/settings/theme-provider";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import { Toaster } from "./components/ui/sonner";
import "./index.css";
import { BaterryPage } from "./pages/battery/BaterryPage";
import BatteryDetail from "./pages/battery/BatteryDetail";
import ChargersDetail from "./pages/chargers/ChargersDetail";
import { ChargersPage } from "./pages/chargers/ChargersPage";
import { FormsAddPage } from "./pages/FormsAddPage";
import Layout from "./pages/Layout";
import { SetupPage } from "./pages/start/SetupPage";
import { StartPage } from "./pages/start/StartPage";

function App() {
  return (
    <ThemeProvider defaultTheme="dark" storageKey="vite-ui-theme">
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Layout />}>
            <Route index element={<StartPage />} />
            <Route path="battery" element={<BaterryPage />} />
            <Route path="battery/:id" element={<BatteryDetail />} />
            <Route path="chargers" element={<ChargersPage />} />
            <Route path="chargers/:id" element={<ChargersDetail />} />
            <Route path="add" element={<FormsAddPage />} />
            <Route path="setup" element={<SetupPage />} />
          </Route>
        </Routes>
      </BrowserRouter>
      <Toaster />
    </ThemeProvider>
  );
}

export default App;
