enum Accumulator = {h, l};
enum Resolution = {h, a, l};
enum FPS = {h, a, l};
enum Weight = {h, a, l};
enum Mount = {no, t1, t2};
enum Isolation = {h, l};

var Accumulator: accumulator;
var Resolution: resolution;
var FPS: fps;
var Weight: weight;
var Mount: mount;
var Isolation: isolation;

constraint buildings == true -> accumulator == Accumulator[h];
constraint people == true -> resolution == Resolution[h];
constraint fastmoves == true -> fps == FPS[h];
constraint easycarry == true -> weight == Weight[l];
constraint flexiblelens == true -> mount != Mount[no];
constraint watertight == true -> isolation == Isolation[h];