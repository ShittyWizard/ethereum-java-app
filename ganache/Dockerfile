# node:alpine will be our base image to create this image
FROM node:alpine
# Set the /app directory as working directory
WORKDIR /app
# Install ganache-cli globally
RUN npm install -g ganache-cli
# Set the default command for the image
CMD ["ganache-cli", "-a", "5", "-m", "pupil warfare desk boss food news trim wet fall modify cave shuffle", "-h", "0.0.0.0"]
