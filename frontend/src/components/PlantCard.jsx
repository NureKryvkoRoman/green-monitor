import React from "react";
import {
  Grid,
  Card,
  CardContent,
  Typography,
  Box,
} from "@mui/material";
import SpaIcon from "@mui/icons-material/Spa";

const PlantCard = ({ plant }) => {
  return (
    <Grid size={{ xs: 12, sm: 6, md: 4 }}>
      <Card
        sx={{
          height: "100%",
          p: 2,
          borderLeft: "6px solid #66bb6a",
          backgroundColor: "#f9fbe7",
        }}
        elevation={3}
      >
        <CardContent>
          <Box display="flex" alignItems="center" mb={1}>
            <SpaIcon color="success" sx={{ mr: 1 }} />
            <Typography variant="h6" fontWeight="bold">
              {plant.name}
            </Typography>
          </Box>

          <Typography variant="body2" style={{ textAlign: "start" }}>
            🌡 <strong>Temperature:</strong> {plant.minTemperature}–{plant.maxTemperature} °C
          </Typography>
          <Typography variant="body2" style={{ textAlign: "start" }}>
            💧 <strong>Humidity:</strong> {plant.minHumidity}–{plant.maxHumidity} %
          </Typography>
          <Typography variant="body2" style={{ textAlign: "start" }}>
            🌱 <strong>Moisture:</strong> {plant.minMoisture}–{plant.maxMoisture} %
          </Typography>
        </CardContent>
      </Card>
    </Grid>
  );
}

export default PlantCard;
