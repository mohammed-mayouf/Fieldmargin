## Task Overview

Following MVVM I Implemented network client connection using Retrofit to fetch a list of certificates from the provided backend. The `CertificatesView` now successfully retrieves data and updates the UI accordingly from the `ApiService`

Added the favourites feature whoch can be viewed using the FAB. Utilised utility functions in the `utils` package for safe api calling and error management.

Added unit tests for the `ApiService` (see `CertificatesRepositoryImpl` for the implementation) covering both successful data fetch scenarios and error handling (happy and unhappy paths).

## Project Structure

- **data**: Network and repository implementations.
- **di**: Dependency injection modules for Retrofit, OkHttp, coroutine dispatchers and repository.
- **domain**: Contains the `GetCertificateUseCase` and interface for the repository.
- **presentation**: ViewModel and UI components (opted for compose).
- **utils**: Helper functions for error handling, date, and currency formatting.


## Further Improvements

- **UI/UX Improvements**: Introduce animations for list item transitions and loading states to enhance user experience.
- **Error Handling UI**: Improve error state UI to include retry mechanisms directly from the screen.
- **Caching Mechanism**: Implement a local database (Room or Realm) for offline access to fetched data.
- **Expand Testing**: Increase test coverage to include UI tests using Espresso using Jacoco and CodeCov.
- **Performance Optimization**: Introduce pagination to optimise app performance. 
- **Defining Errors**: Define errors for multiple usage with the use of well defined sealed class. 


## Testing

Unit tests are included for logic within the codebase.

## Modularization Approach

This project is organized into logical packages for clarity and separation of concerns, can be re-architected to utilise multi-modularisation.
