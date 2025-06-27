import React, { useEffect, useState } from "react";
import {
  Container,
  Grid,
  Typography,
  CircularProgress,
  Box,
} from "@mui/material";
import PlantCard from "../components/PlantCard";

const PlantsList = () => {
  const [plants, setPlants] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch("http://localhost:8080/plant")
      .then((res) => res.json())
      .then((data) => {
        setPlants(data);
        setLoading(false);
      })
      .catch((err) => {
        console.error("Failed to fetch plants:", err);
        setLoading(false);
      });
  }, []);

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" mt={6}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom textAlign="center">
        🌿 Plant Reference List
      </Typography>

      <Grid container spacing={7}>
        {plants.map((p) => (
          <PlantCard plant={p} key={p.id} />
        ))}
      </Grid>
    </Container>
  );
};

export default PlantsList;
