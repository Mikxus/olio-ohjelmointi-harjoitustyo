# olio-ohjelmointi-harjoitustyo
Simple java desktop application with java backend

## Architecture
- Postgress 18 db
- Desktop app Java
    - Javafx + atlantafx
- Backend Java
    - Quarkus providing rest api

## Prod setup
### Authentik Setup:
1. Click new Application for the project

    Path:
    ```Applications/Applications/Create with provider```
2. Application page 
    - Set application name
    - Set slug to backend FQDN
3. Choose a Provider page
    - Select ```Oauth2/OpenID provider```
4. Configure Provider page
    - Set provider name
    - Set ```Authorization flow``` to explicit
    - Set ```Client type``` to public
    - Copy ```Client ID``` to .env's ```AUTHENTIK_CLIENT_ID``` parameter
    - Set ```Redirect URIs/Origins``` to ```regex``` and ```^http://127\.0\.0\.1:(2[0-9]{4}|[3-4][0-9]{4}|50000)/callback$``` **Replace** YOURPORT with the ```AUTHENTIK_CB_PORT``` defined in .env
5. You're done

## Dev setup
Clone git repo
```bash
git clone https://github.com/Mikxus/olio-ohjelmointi-harjoitustyo.git
```

To run locally
```
./gradlew run
```