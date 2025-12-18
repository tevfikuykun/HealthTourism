const { Pact } = require('@pact-foundation/pact');
const path = require('path');

describe('Reservation Service - Doctor Service Contract', () => {
  const provider = new Pact({
    consumer: 'reservation-service',
    provider: 'doctor-service',
    port: 1234,
    log: path.resolve(process.cwd(), 'logs', 'pact.log'),
    dir: path.resolve(process.cwd(), 'pacts'),
    logLevel: 'INFO',
  });

  beforeAll(() => provider.setup());
  afterAll(() => provider.finalize());

  describe('Get doctor consultation fee', () => {
    it('should return doctor consultation fee', async () => {
      await provider.addInteraction({
        state: 'doctor exists',
        uponReceiving: 'a request for doctor consultation fee',
        withRequest: {
          method: 'GET',
          path: '/api/doctors/1/fee',
        },
        willRespondWith: {
          status: 200,
          headers: { 'Content-Type': 'application/json' },
          body: {
            doctorId: 1,
            consultationFee: 500.00,
          },
        },
      });

      // Test implementation
      const response = await fetch('http://localhost:1234/api/doctors/1/fee');
      const data = await response.json();

      expect(data).toEqual({
        doctorId: 1,
        consultationFee: 500.00,
      });
    });
  });
});
