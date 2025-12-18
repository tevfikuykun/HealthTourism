describe('Health Tourism Platform - Edge Cases', () => {
  it('Handles IoT device offline', () => {
    cy.intercept('GET', '**/api/iot/**', { statusCode: 503 }).as('iotOffline');
    
    cy.visit('/super-app');
    cy.get('[data-cy=score-tab]').click();
    
    cy.wait('@iotOffline');
    cy.get('[data-cy=fallback-message]').should('contain', 'IoT data unavailable');
    cy.get('[data-cy=manual-input]').should('be.visible');
  });

  it('Handles blockchain network down', () => {
    cy.intercept('POST', '**/api/blockchain/**', { statusCode: 503 }).as('blockchainDown');
    
    cy.visit('/super-app');
    cy.get('[data-cy=wallet-tab]').click();
    
    cy.wait('@blockchainDown');
    cy.get('[data-cy=offline-verification]').should('be.visible');
  });

  it('Handles payment failure with Saga rollback', () => {
    cy.intercept('POST', '**/api/payment/**', { statusCode: 402 }).as('paymentFailed');
    
    cy.visit('/reservations/new');
    cy.get('[data-cy=create-reservation]').click();
    
    cy.wait('@paymentFailed');
    cy.get('[data-cy=payment-error]').should('be.visible');
    cy.get('[data-cy=rollback-message]').should('contain', 'Reservation cancelled');
  });

  it('Handles service timeout with circuit breaker', () => {
    cy.intercept('GET', '**/api/doctors/**', { 
      delay: 10000,
      statusCode: 504 
    }).as('serviceTimeout');
    
    cy.visit('/doctors');
    
    cy.wait('@serviceTimeout');
    cy.get('[data-cy=circuit-breaker-message]').should('be.visible');
    cy.get('[data-cy=fallback-data]').should('exist');
  });
});
