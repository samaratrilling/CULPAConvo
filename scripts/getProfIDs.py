profs = []

with open('CS_profs.txt', 'rb') as csprofs:
    for line in csprofs:
        name = line.replace("\n", "")
        profs.append(name)

csp = []
firstname = ""
lastname = ""
id = ""


with open('professors.txt', 'rb') as fullfile:
    for line in fullfile:

        if "first_name" in line:
            fn = line.split(":")
            firstname = fn[1].replace(",", "")
            firstname = firstname.replace("\"", "")
            firstname = firstname.strip()
        if "last_name" in line:
            ln = line.split(":")
            lastname = ln[1].replace(",", "")
            lastname = lastname.replace("\"", "")
            lastname = lastname.strip()
        if "\"id\":" in line:
            i = line.split(":")
            id = i[1].replace(",", "")
            id = id.replace("\"", "")
            id = id.strip()
        if "}" in line:
            fullname = firstname + " " + lastname
            fullname = fullname.strip()
            if fullname in profs:
                csp.append(fullname + "," + id)
            firstname = ""
            lastname = ""
            id = ""

with open('CS_profID.txt', 'w') as out:
    for item in csp:
        out.write(item)
        out.write("\n")
