import { BrowserRouter, Routes, Route } from "react-router-dom";
import { ThemeProvider } from "@/components/settings/theme-provider"
import "./index.css"
import { StartPage } from "./pages/start/StartPage"
import Layout from "./pages/Layout";
import { BaterryPage } from "./pages/battery/BaterryPage";
import { ChargersPage } from "./pages/chargers/ChargersPage";
import { BatteryAddPage } from "./pages/battery/BatteryAddPage";
import { FormsAddPage } from "./pages/FormsAddPage";
 


function App() {
  return (
    <ThemeProvider defaultTheme="dark" storageKey="vite-ui-theme">
      <BrowserRouter>
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<StartPage />} />
          <Route path="battery" element={<BaterryPage/>}/>
          <Route path="battery/add" element={<BatteryAddPage/>}/>
          <Route path="chargers" element={<ChargersPage/>}/>
          <Route path="add" element={<FormsAddPage/>}/>
        </Route>
      </Routes>
    </BrowserRouter>
    </ThemeProvider>
  )
}
 
export default App