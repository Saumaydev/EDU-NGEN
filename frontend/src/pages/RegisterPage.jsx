import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Box, Button, TextField, Typography, Container, Alert, Select, MenuItem, FormControl, InputLabel } from '@mui/material';
import apiClient from '../api/apiClient';

const RegisterPage = () => {
  const [userType, setUserType] = useState('STUDENT');
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    fullName: '',
    schoolId: '',
    parentEmail: '',
    classSection: '',
    subject: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await apiClient.post('/auth/register', {
        ...formData,
        userType,
      });
      navigate('/login');
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container maxWidth="sm">
      <Box sx={{ display: 'flex', flexDirection: 'column', justifyContent: 'center', minHeight: '100vh' }}>
        <Typography variant="h3" sx={{ mb: 3, textAlign: 'center' }}>
          EDU-NGEN Registration
        </Typography>

        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

        <form onSubmit={handleRegister}>
          <FormControl fullWidth sx={{ mb: 2 }}>
            <InputLabel>User Type</InputLabel>
            <Select value={userType} onChange={(e) => setUserType(e.target.value)}>
              <MenuItem value="STUDENT">Student</MenuItem>
              <MenuItem value="TEACHER">Teacher</MenuItem>
            </Select>
          </FormControl>

          <TextField
            fullWidth
            label="Email"
            type="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            margin="normal"
            required
          />

          <TextField
            fullWidth
            label="Full Name"
            name="fullName"
            value={formData.fullName}
            onChange={handleChange}
            margin="normal"
            required
          />

          <TextField
            fullWidth
            label="Password"
            type="password"
            name="password"
            value={formData.password}
            onChange={handleChange}
            margin="normal"
            required
          />

          <TextField
            fullWidth
            label="School ID"
            name="schoolId"
            value={formData.schoolId}
            onChange={handleChange}
            margin="normal"
            required
          />

          {userType === 'STUDENT' && (
            <>
              <TextField
                fullWidth
                label="Parent Email"
                type="email"
                name="parentEmail"
                value={formData.parentEmail}
                onChange={handleChange}
                margin="normal"
                required
              />
              <TextField
                fullWidth
                label="Class Section"
                name="classSection"
                value={formData.classSection}
                onChange={handleChange}
                margin="normal"
                required
              />
            </>
          )}

          {userType === 'TEACHER' && (
            <TextField
              fullWidth
              label="Subject"
              name="subject"
              value={formData.subject}
              onChange={handleChange}
              margin="normal"
              required
            />
          )}

          <Button
            fullWidth
            variant="contained"
            sx={{ mt: 3, mb: 2 }}
            type="submit"
            disabled={loading}
          >
            {loading ? 'Registering...' : 'Register'}
          </Button>
        </form>
      </Box>
    </Container>
  );
};

export default RegisterPage;
