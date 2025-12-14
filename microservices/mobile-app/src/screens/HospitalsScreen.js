import React, { useState, useEffect } from 'react';
import { View, Text, StyleSheet, FlatList, TouchableOpacity, ActivityIndicator } from 'react-native';
import Icon from 'react-native-vector-icons/MaterialIcons';
import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

export default function HospitalsScreen({ navigation }) {
  const [hospitals, setHospitals] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchHospitals();
  }, []);

  const fetchHospitals = async () => {
    try {
      const response = await axios.get(`${API_URL}/hospitals`);
      setHospitals(response.data);
    } catch (error) {
      console.error('Error fetching hospitals:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <View style={styles.center}>
        <ActivityIndicator size="large" color="#1976d2" />
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <FlatList
        data={hospitals}
        keyExtractor={(item) => item.id.toString()}
        renderItem={({ item }) => (
          <TouchableOpacity style={styles.hospitalCard}>
            <Icon name="local-hospital" size={30} color="#1976d2" />
            <View style={styles.hospitalInfo}>
              <Text style={styles.hospitalName}>{item.name}</Text>
              <Text style={styles.hospitalLocation}>{item.location}</Text>
              <Text style={styles.hospitalRating}>
                <Icon name="star" size={16} color="#FFD700" /> {item.rating || '4.5'}
              </Text>
            </View>
            <Icon name="chevron-right" size={24} color="#999" />
          </TouchableOpacity>
        )}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  center: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  hospitalCard: {
    backgroundColor: 'white',
    flexDirection: 'row',
    padding: 15,
    margin: 10,
    borderRadius: 10,
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  hospitalInfo: {
    flex: 1,
    marginLeft: 15,
  },
  hospitalName: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
  },
  hospitalLocation: {
    fontSize: 14,
    color: '#666',
    marginTop: 5,
  },
  hospitalRating: {
    fontSize: 14,
    color: '#666',
    marginTop: 5,
  },
});

