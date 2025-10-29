import * as React from "react";
import { useNavigate } from "react-router-dom";
import AppBar from "@mui/material/AppBar";
import Box from "@mui/material/Box";
import Toolbar from "@mui/material/Toolbar";
import IconButton from "@mui/material/IconButton";
import Typography from "@mui/material/Typography";
import Menu from "@mui/material/Menu";
import MenuItem from "@mui/material/MenuItem";
import Container from "@mui/material/Container";
import MenuIcon from "@mui/icons-material/Menu";
import logo from "../assets/logo.png"
import login from "../assets/login_log.png";
import register from "../assets/register_logo.png";
import landing from "../assets/landing.png";
export default function PublicAppBar(){
const navigate = useNavigate();

  // States for mobile menu
  const [anchorElNav, setAnchorElNav] = React.useState(null);

  const handleOpenNavMenu = (event) => {
    setAnchorElNav(event.currentTarget);
  };
const handleCloseNavMenu = () => {
    setAnchorElNav(null);
  };

return (
<>
 <AppBar sx={{ backgroundColor: "white", boxShadow: 2 }}>
        <Container maxWidth="xl">
          <Toolbar disableGutters>
            {/* Left Section: Logo */}
            <Box
              sx={{
                display: "flex",
                alignItems: "center",
                flexGrow: 1,
                cursor: "pointer",
              }}
              onClick={() => navigate("/")}
            >
              <img
                src={logo}
                alt="logo"
                style={{ width: 80, height: 70, marginRight: 8 }}
              />
            </Box>

            {/* Desktop Menu (hidden on small screens) */}
            <Box sx={{ display: { xs: "none", md: "flex" }, gap: 3 }}>
              <IconButton
                color="inherit"
                sx={{ flexDirection: "column" }}
                onClick={() => navigate("/register")}
              >
                <img
                  src={register}
                  alt="register_logo"
                  style={{ width: 30, height: 30 }}
                />
                <Typography
                  variant="caption"
                  sx={{
                    color: "rgb(21 2 2 / 99%)",
                    fontWeight: 600,
                    letterSpacing: ".1rem",
                  }}
                >
                  Registration
                </Typography>
              </IconButton>

              <IconButton
                color="inherit"
                sx={{ flexDirection: "column" }}
                onClick={() => navigate("/login")}
              >
                <img
                  src={login}
                  alt="login_logo"
                  style={{ width: 30, height: 30 }}
                />
                <Typography
                  variant="caption"
                  sx={{
                    color: "rgb(21 2 2 / 99%)",
                    fontWeight: 600,
                    letterSpacing: ".1rem",
                  }}
                >
                  Login
                </Typography>
              </IconButton>
            </Box>

            {/* Mobile Menu Icon */}
            <Box sx={{ display: { xs: "flex", md: "none" } }}>
              <IconButton onClick={handleOpenNavMenu}>
                <MenuIcon sx={{ color: "black" }} />
              </IconButton>

              {/*  Mobile Dropdown Menu */}
              <Menu
                anchorEl={anchorElNav}
                open={Boolean(anchorElNav)}
                onClose={handleCloseNavMenu}
              >
                <MenuItem
                  onClick={() => {
                    handleCloseNavMenu();
                    navigate("/register");
                  }}
                >
                  Registration
                </MenuItem>
                <MenuItem
                  onClick={() => {
                    handleCloseNavMenu();
                    navigate("/login");
                  }}
                >
                  Login
                </MenuItem>
              </Menu>
            </Box>
          </Toolbar>
        </Container>
      </AppBar>
</>
    );

}