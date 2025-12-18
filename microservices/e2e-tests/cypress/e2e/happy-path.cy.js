describe('Health Tourism Platform - Happy Path', () => {
  beforeEach(() => {
    // Login before each test
    cy.visit('/login');
    cy.get('[data-cy=email]').type('test@example.com');
    cy.get('[data-cy=password]').type('password123');
    cy.get('[data-cy=login-button]').click();
    cy.url().should('include', '/dashboard');
  });

  it('Complete reservation flow', () => {
    // 1. Search for hospitals
    cy.visit('/hospitals');
    cy.get('[data-cy=search-input]').type('Istanbul');
    cy.get('[data-cy=search-button]').click();
    cy.get('[data-cy=hospital-card]').first().click();

    // 2. Select doctor
    cy.get('[data-cy=doctor-card]').first().click();
    cy.get('[data-cy=book-appointment]').click();

    // 3. Select accommodation
    cy.visit('/accommodations');
    cy.get('[data-cy=accommodation-card]').first().click();
    cy.get('[data-cy=select-dates]').click();
    cy.get('[data-cy=check-in]').type('2024-02-01');
    cy.get('[data-cy=check-out]').type('2024-02-05');
    cy.get('[data-cy=book-accommodation]').click();

    // 4. Book flight
    cy.visit('/flights');
    cy.get('[data-cy=flight-search]').type('London');
    cy.get('[data-cy=search-flight]').click();
    cy.get('[data-cy=flight-card]').first().click();
    cy.get('[data-cy=book-flight]').click();

    // 5. Create reservation
    cy.visit('/reservations/new');
    cy.get('[data-cy=create-reservation]').click();
    cy.get('[data-cy=reservation-success]').should('be.visible');

    // 6. Verify reservation
    cy.visit('/reservations');
    cy.get('[data-cy=reservation-card]').should('exist');
  });

  it('Post-op monitoring flow', () => {
    // 1. Access health wallet
    cy.visit('/super-app');
    cy.get('[data-cy=wallet-tab]').click();
    cy.get('[data-cy=qr-code]').should('be.visible');

    // 2. Check recovery score
    cy.get('[data-cy=score-tab]').click();
    cy.get('[data-cy=recovery-score]').should('exist');
    cy.get('[data-cy=score-explanation]').should('exist');

    // 3. AI Health Companion
    cy.get('[data-cy=ai-tab]').click();
    cy.get('[data-cy=chat-input]').type('How is my recovery going?');
    cy.get('[data-cy=send-button]').click();
    cy.get('[data-cy=ai-response]').should('exist');
  });
});
