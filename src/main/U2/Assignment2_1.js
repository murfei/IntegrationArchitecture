// 1) Object creation

//Object Literal
const salesMan = {
    firstName: "John",
    lastName: "Doe",
    employee_id: 9999,
    nationality: "German",
    performanceRecords: []
};

// Constructor function
function SalesMan(firstName, lastName, employee_id) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.employee_id = employee_id;
    this.performanceRecords = [];
}
SalesMan.prototype.addRecord = function(record){ this.performanceRecords.push(record); };

const s = new SalesMan("Jane","Smith", 101);

// ES6 class
class SalesMan {
    constructor(firstName, lastName, employee_id) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.employee_id = employee_id;
        this.performanceRecords = [];
    }

    addPerformanceRecord(record) {
        this.performanceRecords.push(record);
    }
}
var s = new SalesMan("Jane","Smith", 101);
s.addPerformanceRecord("Jane");
console.log(s.performanceRecords[0]);
//*****************************************************************************************************

//*****************************************************************************************************
// 2) Functions and callbacks (and the “Pyramid of Doom”)
//Function:
function fun(param1) {
    console.log(param1);
}
fun(1);

//A callback is a function that is passed as an argument to another function and is executed later.
function doSomething(callback) {
    console.log("Doing something...");
    callback(); // Run the function that was passed in
}

function f() {console.log("This is the callback function!");
}
doSomething(f);

// callback style -> nested callbacks
db.collection('salesmen').findOne({ employee_id: 101 }, function(err, doc) {
    if (err) throw err;
    db.collection('other').findOne({ id: doc.someId }, function(err2, o) {
        if (err2) throw err2;
        // keeps nesting...
    });
});
// Pyramid shape --> hard to read, debug
task1(function() {
    task2(function() {
        task3(function() {
            task4(function() {
                // too deeply nested...
            });
        });
    });
});
//*****************************************************************************************************

//*****************************************************************************************************
/* 3) Promises (including error handling)
A Promise is an object that represents a task that happens in the future like finding a document in a DB.

It can have 3 states:
 Pending → Still working.
 Fulfilled → Finished successfully.
 Rejected → Failed with an error.

.then() is used to handle the result when a Promise is fulfilled.
.catch() is used to handle the error when a Promise is rejected.
 */

function findSalesman(collection, employeeId) {
    return collection.findOne({ employee_id: employeeId });
}

// Usage with then/catch:
findSalesman(collection, 101)
    .then(function(doc) {
        if (!doc) throw new Error("not found");
        console.log("found", doc.firstName);
    })
    .catch(function(err) {
        console.error("error", err);
    });
//*****************************************************************************************************

//*****************************************************************************************************
//4) async / await
// Modern and readable — implicitly works with Promises.
// Async/await statements make working with Promises simpler, cleaner, and easier to debug. All code can be put into the
// try block without having to create multiple functions. In addition the code is still non-blocking, meaning the program
// doesn't have to wait for a long operation (e.g. reading a DB) to finish before it continues executing other code.

async function getSalesmanById(collection, employeeId) {
    try {
        const doc = await collection.findOne({ employee_id: employeeId });
        if (!doc) return null;
        return doc;
    } catch (err) {
        console.error("DB error", err);
        throw err;
    }
}
//call
(async function(){
    const s = await getSalesmanById(collection, 101);
    console.log(s);
})();
const express = require('express');