import React from 'react';
import { Link } from 'react-router';

const NotFound = () => {
  return (
    <div className="flex flex-col items-center justify-center min-h-screen text-center bg-gray-100 px-4">
      <h1 className="text-6xl font-bold text-green-700 mb-4">404</h1>
      <p className="text-xl text-gray-600 mb-6">
        Oops! The page you're looking for doesn't exist.
      </p>
      <Link
        to="/dashboard"
        className="px-6 py-2 bg-green-600 text-white rounded-full hover:bg-green-700 transition"
      >
        Go to Dashboard
      </Link>
    </div>
  );
};

export default NotFound;
