#ifndef TYPES_H
#define TYPES_H

// Estados das vias
typedef enum {
    TRACK_FREE = 0,
    TRACK_ASSIGNED = 1,
    TRACK_BUSY = 2,
    TRACK_INOPERATIVE = 3
} TrackState;

#endif