import { configureStore } from '@reduxjs/toolkit';
import authReducer from './slices/authSlice';
import examReducer from './slices/examSlice';
import studentReducer from './slices/studentSlice';

const store = configureStore({
  reducer: {
    auth: authReducer,
    exam: examReducer,
    student: studentReducer,
  },
});

export default store;
