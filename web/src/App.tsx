import { BrowserRouter, Routes, Route } from "react-router-dom";
import { ThemeProvider } from "@/components/settings/theme-provider"
import "./index.css"
import { StartPage } from "./pages/start/StartPage"
import Layout from "./pages/Layout";
import { BaterryPage } from "./pages/battery/BaterryPage";
import { ChargersPage } from "./pages/chargers/ChargersPage";
import { FormsAddPage } from "./pages/FormsAddPage";
import { SetupPage } from "./pages/start/SetupPage";
 


function App() {
  return (
    <ThemeProvider defaultTheme="dark" storageKey="vite-ui-theme">
      <BrowserRouter>
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<StartPage />} />
          <Route path="battery" element={<BaterryPage/>}/>
          <Route path="chargers" element={<ChargersPage/>}/>
          <Route path="add" element={<FormsAddPage/>}/>
          <Route path="setup" element={<SetupPage/>}/>
        </Route>
      </Routes>
    </BrowserRouter>
    </ThemeProvider>
  )
}
 
export default App