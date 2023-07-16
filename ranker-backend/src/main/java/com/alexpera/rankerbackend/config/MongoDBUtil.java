package com.alexpera.rankerbackend.config;

import com.alexpera.rankerbackend.model.anilist.Edge;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;


@Slf4j
public class MongoDBUtil {
    @Getter
    private static MongoCollection<Edge> edges;

    private static MongoDatabase db;


    public static void init() {
        CodecRegistry registry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(new ConnectionString("mongodb://localhost:27017")).codecRegistry(registry).build();

        MongoClient client = MongoClients.create(settings);

        db = client.getDatabase("flightsystem").withCodecRegistry(registry);

        edges = db.getCollection("edges", Edge.class);
        log.info("MongoDBUtil initialized");
    }
}
