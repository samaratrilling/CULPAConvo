import string
print("Make sure the vocab file is in the directory above scripts/")
vocab_name = "../" + input("Enter the name of the vocab file: ")
text_name = "../" + input("Enter a name for the new text file: ")
with open(vocab_name) as vocab_file, open(text_name, 'w') as text_file:
    for line in vocab_file:
        line = line.replace("<phrase>", "")
        line = line.replace("<phrases>", "")
        line = line.replace("</phrase>", "")
        line = line.replace("</phrases>", "")
        if line != "\n":
            text_file.write(line)

vocab_file.close()
text_file.close()



