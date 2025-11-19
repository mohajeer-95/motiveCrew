FROM openjdk:21

# Create directory
RUN mkdir /usr/local/CXM/

# Set working directory
WORKDIR /usr/local/CXM/

# Copy the JAR files to the specified directory
COPY ./build/libs/ /usr/local/CXM/

# Set permissions on the JAR files
RUN chmod -R 777 /usr/local/CXM/*

# Expose the required port
EXPOSE 9999

# Define arguments for Spring profile and Liquibase
ARG SPRING_PROFILE=qa
ARG LIQUIBASE_ENABLED=false

# Set environment variables
ENV SPRING_PROFILE=${SPRING_PROFILE}
ENV LIQUIBASE_ENABLED=${LIQUIBASE_ENABLED}

# Use environment variables in CMD
CMD ["sh", "-c", "java -Duser.timezone=Asia/Kuwait -jar /usr/local/CXM/cxm-ws.jar --spring.profiles.active=${SPRING_PROFILE} --spring.liquibase.enabled=${LIQUIBASE_ENABLED}"]